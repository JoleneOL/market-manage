package me.jiangcai.logistics.haier.service;

import lombok.SneakyThrows;
import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsSource;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Distribution;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.exception.SupplierException;
import me.jiangcai.logistics.haier.HaierSupplier;
import me.jiangcai.logistics.haier.http.ResponseHandler;
import me.jiangcai.logistics.haier.model.OrderStatusSync;
import me.jiangcai.logistics.haier.model.OutInStore;
import me.jiangcai.logistics.option.LogisticsOptions;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Component
public class HaierSupplierImpl implements HaierSupplier {

    private static final Log log = LogFactory.getLog(HaierSupplierImpl.class);

    private final String gateway;
    private final String keyValue;
    private final Key key;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @Autowired
    public HaierSupplierImpl(Environment environment) throws UnsupportedEncodingException {
        this.gateway = environment.getProperty("haier.gateway.URL", "http://58.56.128.84:9001/EAI/service/VOM/CommonGetWayToVOM/CommonGetWayToVOM");
        this.keyValue = environment.getProperty("haier.keyValue", "RRS,123");// KeLy8g7qjmnbgWP1
        this.key = new SecretKeySpec(environment.getProperty("haier.key", "KeLy8g7qjmnbgWP1").getBytes("UTF-8"), "AES");
        // Haier,123
    }

    @Override
    public void cancelOrder(String id, boolean focus, String reason) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orderno", id);
        parameters.put("canceltype", focus ? "2" : "1");
        if (!StringUtils.isEmpty(reason))
            parameters.put("cancelexplain", reason);
        sendRequest(parameters, "rrs_cancel");
    }

    @Override
    public void updateProduct(Product product) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("productcode", product.getCode());
        parameters.put("brand", product.getBrand());
        parameters.put("category", product.getMainCategory());
        parameters.put("prodes", product.getDescription());
        parameters.put("skubarcode", product.getSKU());
        parameters.put("unit", product.getUnit());
        parameters.put("length", product.getVolumeLength());
        parameters.put("width", product.getVolumeWidth());
        parameters.put("height", product.getVolumeHeight());
        parameters.put("volume", product.getVolume());
        parameters.put("weight", product.getWeight().movePointLeft(3));

        sendRequest(parameters, "rrs_productdata");
    }

    @Override
    public StockShiftUnit makeDistributionOrder(LogisticsSource source, Collection<? extends Thing> things
            , LogisticsDestination destination, int options, Consumer<StockShiftUnit> consumer) {
        Map<String, Object> parameters = new HashMap<>();

        String id = UUID.randomUUID().toString().replaceAll("-", "");

        parameters.put("orderno", id);
        parameters.put("sourcesn", id);
        if ((options & LogisticsOptions.CargoFromStorage) == LogisticsOptions.CargoFromStorage) {
            parameters.put("ordertype", "2");
            parameters.put("bustype", "2");
        }
        if ((options & LogisticsOptions.CargoToStorage) == LogisticsOptions.CargoToStorage) {
            parameters.put("ordertype", "1");
            parameters.put("bustype", "1");
        }
        parameters.put("expno", id);// 快递单号：自动分配的快递单号或客户生成的快递单号
        parameters.put("orderdate", LocalDateTime.now().format(formatter));
        parameters.put("storecode", ((Storage) source).getStorageCode());

        parameters.put("province", destination.getProvince());
        parameters.put("city", destination.getCity());
        parameters.put("county", destination.getCountry());
        parameters.put("addr", destination.getDetailAddress());
        parameters.put("name", destination.getConsigneeName());
        parameters.put("mobile", destination.getConsigneeMobile());

        parameters.put("busflag", (options & LogisticsOptions.Installation) == LogisticsOptions.Installation ? "1" : "2");

        List<Map<String, Object>> items = things.stream()
                .map(this::toItemData)
                .collect(Collectors.toList());
        for (int i = 0; i < items.size(); i++) {
            items.get(i).put("itemno", i + 1);
        }

        parameters.put("items", items);
        //参数准备完成
        sendRequest(parameters, "rrs_order");

        Distribution distribution = new Distribution();
        distribution.setId(id);
        return distribution;

    }

    private void sendRequest(Map<String, Object> parameters, String type) {
        try {

            String content = objectMapper.writeValueAsString(parameters);
            log.debug("prepare content:" + content);

            try (CloseableHttpClient client = newClient()) {
                HttpPost post = new HttpPost(gateway);
                post.setEntity(
                        EntityBuilder.create()
                                .setContentType(ContentType.APPLICATION_FORM_URLENCODED.withCharset(Charset.forName("UTF-8")))
                                .setParameters(
                                        new BasicNameValuePair("notifyid", UUID.randomUUID().toString().replaceAll("-", ""))
                                        , new BasicNameValuePair("notifytime", LocalDateTime.now().format(formatter))
                                        , new BasicNameValuePair("butype", type)
                                        , new BasicNameValuePair("source", "LIMEIJIA")
                                        , new BasicNameValuePair("type", "Json")
                                        , new BasicNameValuePair("sign", sign(content, keyValue))
                                        , new BasicNameValuePair("content", cipherEncrypt(content))
                                )
                                .build()
                );

                client.execute(post, new ResponseHandler());


            }
        } catch (IOException e) {
            throw new SupplierException(e);
        }
    }

    @SneakyThrows({NoSuchAlgorithmException.class, NoSuchPaddingException.class, InvalidKeyException.class
            , UnsupportedEncodingException.class, BadPaddingException.class, IllegalBlockSizeException.class})
    private String cipherEncrypt(String content) {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        final String encodeToString = Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes("UTF-8")));
        log.debug("密文：" + encodeToString);
        return encodeToString;
    }

    @SneakyThrows({NoSuchAlgorithmException.class, NoSuchPaddingException.class, InvalidKeyException.class
            , UnsupportedEncodingException.class, BadPaddingException.class, IllegalBlockSizeException.class})
    private String cipherDecrypt(String content) {
        byte[] data = Base64.getDecoder().decode(content);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        data = cipher.doFinal(data);
        String string = new String(data, "UTF-8");
        log.debug("明文：" + string);
        return string;
    }

    //NoSuchAlgorithmException.class,
    // UnsupportedEncodingException.class
    @Override
    @SneakyThrows({})
    public String sign(String content, String keyValue) {
        final String hex = DigestUtils.md5Hex(content + keyValue);
        final String sign = Base64.getEncoder().encodeToString(hex.getBytes("UTF-8"));
        log.debug("sign:" + sign);
        return sign;
    }

    @Override
    public Object event(String businessType, String source, String contentType, String sign, String content) throws IOException {
        // <Return/>
        log.debug("businessType:" + businessType);
        content = cipherDecrypt(content);
        if (!sign.equals(sign(content, keyValue)))
            throw new IllegalArgumentException("Bad Sign.");
        if (!source.equals("LIMEIJIA"))
            throw new IllegalArgumentException("Bad Source:" + source);

        if ("rrs_outinstore".equalsIgnoreCase(businessType)) {
            outInStoreEvent(contentType, content);
        } else if ("rrs_statusback".equalsIgnoreCase(businessType)) {
            statusBack(contentType, content);
        } else
            throw new IllegalArgumentException("not support businessType:" + businessType);


//        return "中文" + content;
        return null;
    }

    private <T> T toModel(Class<T> javaType, String contentType, String content) throws IOException {
        T requestData;
        if (contentType.equalsIgnoreCase("xml")) {
            //noinspection unchecked
            requestData = xmlMapper.readValue(content, javaType);
        } else if (contentType.equalsIgnoreCase("json")) {
            //noinspection unchecked
            requestData = objectMapper.readValue(content, javaType);
        } else
            throw new IllegalArgumentException("bad type:" + contentType);

//        System.out.println(requestData);
        log.trace(requestData);
        return requestData;
    }

    private void outInStoreEvent(String contentType, String content) throws IOException {
        OutInStore outInStore = toModel(OutInStore.class, contentType, content);
        // 处理该事件！
        applicationEventPublisher.publishEvent(outInStore);
    }

    private void statusBack(String contentType, String content) throws IOException {
        OrderStatusSync sync = toModel(OrderStatusSync.class, contentType, content);
        applicationEventPublisher.publishEvent(sync);
    }

    private CloseableHttpClient newClient() {
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setSocketTimeout(30000)
                        .setConnectionRequestTimeout(30000)
                        .setConnectTimeout(30000)
                        .build())
                .build();
    }

    private Map<String, Object> toItemData(Thing thing) {
        Map<String, Object> data = new HashMap<>();
        data.put("storagetype", "10");
        data.put("productcode", thing.getProductCode());
        data.put("prodes", thing.getProductName());
        data.put("number", thing.getAmount());
        return data;
    }
}
