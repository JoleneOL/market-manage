package cn.lmjia.payment.support.huabei.service;

import cn.lmjia.payment.support.huabei.HuabeiPaymentForm;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author CJ
 */
@Service
public class HuabeiPaymentFormImpl implements HuabeiPaymentForm {

    /**
     * 通讯URL
     */
    private final String rootUrl;
    /**
     * 商户编码
     */
    private final String businessID;
    /**
     * 收款门店编码
     */
    private final String shopID;
    /**
     * 收款支付宝PID
     */
    private final String aliPid;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");
    private final Random random = new Random();
    @Autowired
    public HuabeiPaymentFormImpl(Environment environment) {
        rootUrl = environment.getProperty("huabei.url", "http://hbfq.huaat.com");
        businessID = environment.getProperty("huabei.businessID", "HBCD2040");
        shopID = environment.getProperty("huabei.shopID", "HBCD20400001");
        aliPid = environment.getProperty("huabei.aliPid", "");
    }

    private String createOrderId() {
        return String.format("%s%-5d", LocalDateTime.now().format(dateTimeFormatter), Math.abs(random.nextInt()));
    }

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order
            , Map<String, Object> additionalParameters) throws SystemMaintainException {
        // 商户预创建订单时候的订单号，将作为本订单的唯一标识格式为:yyMMddHHmmssSSS+5位随机数
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("businessID", businessID);
        parameters.put("ShopID", shopID);
        parameters.put("aliPid", aliPid);
        String orderID = createOrderId();
        parameters.put("orderID", orderID);


        try {
            postJSON("/payAPI/createOrder", parameters);
        } catch (IOException e) {
            throw new SystemMaintainException(e);
        }

        return null;
    }

    private CloseableHttpClient requestClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder = builder.setDefaultRequestConfig(RequestConfig.custom()
                .setConnectTimeout(30000)
                .setConnectionRequestTimeout(30000)
                .setSocketTimeout(300000)
                .build());
//        if (environment.acceptsProfiles("test")) {
//            builder.setSSLHostnameVerifier(new NoopHostnameVerifier());
//        }

        return builder.build();
    }


    private void postJSON(String uri, Map<String, Object> parameters) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpPost post = new HttpPost(rootUrl + uri);
            post.setEntity(
                    EntityBuilder.create()
                            .setParameters(new BasicNameValuePair("orderDetail"
                                    , JsonHandler.objectMapper.writeValueAsString(parameters)))
                            .build()
            );
            client.execute(post, new JsonHandler());
        }
    }

    @Override
    public void orderMaintain() {

    }
}
