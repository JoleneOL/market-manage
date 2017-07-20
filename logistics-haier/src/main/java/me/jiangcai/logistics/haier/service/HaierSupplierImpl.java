package me.jiangcai.logistics.haier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import me.jiangcai.logistics.Destination;
import me.jiangcai.logistics.Source;
import me.jiangcai.logistics.Storage;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Distribution;
import me.jiangcai.logistics.exception.SupplierException;
import me.jiangcai.logistics.haier.HaierSupplier;
import me.jiangcai.logistics.haier.http.ResponseHandler;
import me.jiangcai.logistics.option.LogisticsOptions;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Component
public class HaierSupplierImpl implements HaierSupplier {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final String gateway;
    private final String keyValue;

    @Autowired
    public HaierSupplierImpl(Environment environment) {
        this.gateway = environment.getProperty("haier.gateway.URL", "http://58.56.128.84:9001/EAI/service/VOM/CommonGetWayToVOM/CommonGetWayToVOM");
        this.keyValue = environment.getProperty("haier.keyValue", "Haier,123");
    }

    @Override
    public Distribution makeDistributionOrder(Source source, Collection<Thing> things, Destination destination, int options) {
        Map<String, Object> parameters = new HashMap<>();

        String id = UUID.randomUUID().toString().replaceAll("-", "");

        parameters.put("orderno", id);
        parameters.put("sourcesn", id);
        if ((options & LogisticsOptions.CargoFromStorage) == LogisticsOptions.CargoFromStorage) {
            parameters.put("ordertype", "3");
            parameters.put("bustype", "2");
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
        try {

            String content = objectMapper.writeValueAsString(parameters);

            try (CloseableHttpClient client = newClient()) {
                HttpPost post = new HttpPost(gateway);
                post.setEntity(
                        EntityBuilder.create()
                                .setContentType(ContentType.APPLICATION_FORM_URLENCODED.withCharset(Charset.forName("UTF-8")))
                                .setParameters(
                                        new BasicNameValuePair("notifyid", UUID.randomUUID().toString().replaceAll("-", ""))
                                        , new BasicNameValuePair("notifytime", LocalDateTime.now().format(formatter))
                                        , new BasicNameValuePair("butype", "rrs_order")
                                        , new BasicNameValuePair("source", "JSH")
                                        , new BasicNameValuePair("type", "Json")
                                        , new BasicNameValuePair("sign", sign(content))
                                        , new BasicNameValuePair("content", content)
                                )
                                .build()
                );

                client.execute(post, new ResponseHandler());

                Distribution distribution = new Distribution();
                distribution.setId(id);
                return distribution;
            }
        } catch (IOException e) {
            throw new SupplierException(e);
        }

    }

    @SneakyThrows({NoSuchAlgorithmException.class, UnsupportedEncodingException.class})
    private String sign(String content) {
        /*
        "签名 base64(MD5(content+keyValue))
ketValue：Haier,123
content：如下"
         */
        byte[] digest = MessageDigest.getInstance("MD5").digest((content + keyValue).getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(digest);
    }

    private CloseableHttpClient newClient() {
        return HttpClientBuilder.create()
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
