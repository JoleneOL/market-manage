package cn.lmjia.market.core.trj.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.trj.AuthorisingInfo;
import cn.lmjia.market.core.entity.trj.TRJPayOrder;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.repository.trj.AuthorisingInfoRepository;
import cn.lmjia.market.core.trj.InvalidAuthorisingException;
import cn.lmjia.market.core.trj.TRJService;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.event.OrderPaySuccess;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.service.PaymentService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author CJ
 */
@Service
public class TRJServiceImpl implements TRJService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Log log = LogFactory.getLog(TRJServiceImpl.class);
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private final String urlRoot;
    private final String tenant;
    private final String key;
    @Autowired
    private AuthorisingInfoRepository authorisingInfoRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MainOrderRepository mainOrderRepository;

    @Autowired
    public TRJServiceImpl(Environment environment) {
        urlRoot = environment.getProperty("me.jiangcai.dating.tourongjia.url2", "http://escrowcrm1.tourongjia.com");
        tenant = environment.getProperty("me.jiangcai.dating.tourongjia.tenant", "yuntao");
        key = environment.getProperty("me.jiangcai.dating.tourongjia.key", "1234567890");
    }

    private static String sign(final Map<String, String> params, String secretKey) {

        if (StringUtils.isBlank(secretKey)) {
            throw new IllegalArgumentException("secretKey not bank.");
        }

        StringBuilder sb = new StringBuilder();

        if (params != null && params.size() > 0) {

            List<String> nameList = new ArrayList<>();

            for (Iterator<String> it = params.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();
                nameList.add(key);
            }

            Collections.sort(nameList);

            for (String name : nameList) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(name).append("=").append(params.get(name));
            }

        }

        sb.append(secretKey);

        return DigestUtils.md5Hex(sb.toString());
    }

    @Override
    public void addAuthorisingInfo(String authorising, String idNumber) {
        AuthorisingInfo info = new AuthorisingInfo();
        info.setId(authorising);
        info.setIdNumber(idNumber);
        info.setCreatedTime(LocalDateTime.now());
        authorisingInfoRepository.save(info);
    }

    private void submitOrderInfo(MainOrder order, AuthorisingInfo info) {
        // 使用脚本运作
        try {
            final Login guideUser = order.getOrderBy().getGuideUser();
            Long recommendId;
            if (guideUser != null)
                recommendId = guideUser.getId();
            else
                recommendId = 0L;
            submitOrderInfo(info.getId(), order.getId(), order.getCustomer().getName(), info.getIdNumber()
                    , order.getCustomer().getMobile(), order.getGood().getProduct().getCode()
                    , order.getGood().getProduct().getName(), order.getAmount()
                    , order.getOrderDueAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString()
                    , order.getInstallAddress().toTRJString(), order.getOrderTime().format(formatter)
                    , recommendId);
        } catch (Exception e) {
            // 提交任务
            log.debug("[TRJ]", e);
        }
    }

    private void submitOrderInfo(String authorising, Long orderId, String name, String idNumber, String mobile
            , String goodCode, String goodName, int amount, String dueAmount, String address, String orderTime
            , Long recommendCode) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            client.execute(newUriRequest("/ApiServer/tenant/goods_authorisingOrderInfo.jhtml"
                    , new BasicNameValuePair("authorising", authorising)
                    , new BasicNameValuePair("orderId", String.valueOf(orderId))
                    , new BasicNameValuePair("orderTenant.name", name)
                    , new BasicNameValuePair("orderTenant.identityId", idNumber)
                    , new BasicNameValuePair("orderTenant.mobile", mobile)
                    , new BasicNameValuePair("orderTenant.goodType", goodCode)
                    , new BasicNameValuePair("orderTenant.goodName", goodName)
                    , new BasicNameValuePair("orderTenant.quantity", String.valueOf(amount))
                    , new BasicNameValuePair("orderTenant.orderAmount", dueAmount)
                    , new BasicNameValuePair("orderTenant.installAddress", address)
                    , new BasicNameValuePair("orderTenant.orderTime", orderTime)
                    , new BasicNameValuePair("orderTenant.recommendCode", String.valueOf(recommendCode))
            ), new StrangeJsonHandler<>(Void.class));
        }
    }

    private HttpUriRequest newUriRequest(String uri, NameValuePair... pairs) {
        return newUriRequest(uri, null, pairs);
    }

    private HttpUriRequest newUriRequest(String uri, HttpEntity entity, NameValuePair... pairs) {
        List<NameValuePair> list = new ArrayList<>();
        list.addAll(Arrays.asList(pairs));
        list.add(new BasicNameValuePair("tenant", tenant));
        list.add(new BasicNameValuePair("sign", sign(list)));
        // 串接
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(urlRoot).append(uri).append("?");
        list.forEach(nameValuePair -> {
            try {
                urlBuilder.append(nameValuePair.getName()).append("=").append(URLEncoder.encode(nameValuePair.getValue(), "UTF-8"))
                        .append("&");
            } catch (UnsupportedEncodingException e) {
                throw new InternalError(e);
            }
        });

        urlBuilder.setLength(urlBuilder.length() - 1);
        if (log.isDebugEnabled())
            log.debug("[TRJ]" + urlBuilder.toString());
        HttpPost post = new HttpPost(urlBuilder.toString());
        if (entity == null)
            return post;
        post.setEntity(entity);
        return post;
    }

    private String sign(List<NameValuePair> list) {
        HashMap<String, String> data = new HashMap<>();
        list.forEach(nameValuePair -> data.put(nameValuePair.getName(), nameValuePair.getValue()));
        return sign(data, key);
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

    @EventListener(OrderPaySuccess.class)
    @Override
    public void paySuccess(OrderPaySuccess event) {
        if ((event.getPayableOrder() instanceof MainOrder) && (event.getPayOrder() instanceof TRJPayOrder)) {
            // 发布事件
            MainOrder mainOrder = (MainOrder) event.getPayableOrder();
            TRJPayOrder payOrder = (TRJPayOrder) event.getPayOrder();
            submitOrderInfo(mainOrder, payOrder.getAuthorisingInfo());
        }
    }

    @Override
    public AuthorisingInfo checkAuthorising(String authorising, String idNumber) throws InvalidAuthorisingException {
        AuthorisingInfo info = authorisingInfoRepository.findOne(authorising);
        if (info == null)
            throw new InvalidAuthorisingException(authorising, idNumber);
        if (info.isUsed())
            throw new InvalidAuthorisingException(authorising, idNumber);
        return info;
    }

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order
            , Map<String, Object> additionalParameters) throws SystemMaintainException {
        // 立即完成支付，同时告诉系统这个订单不能被结算！
        AuthorisingInfo info = (AuthorisingInfo) additionalParameters.get("info");
        synchronized (("AuthorisingInfo-" + info.getId()).intern()) {
            info = authorisingInfoRepository.getOne(info.getId());
            if (info.isUsed())
                throw new SystemMaintainException(new InvalidAuthorisingException(info.getId(), info.getIdNumber()));
            info.setUsed(true);
            info.setUsedTime(LocalDateTime.now());
            info = authorisingInfoRepository.save(info);
            TRJPayOrder payOrder = new TRJPayOrder();
            payOrder.setPlatformId(info.getId());
            payOrder.setAuthorisingInfo(info);
            // 需要立即完成支付
            executorService.schedule(()
                            -> applicationContext.getBean(PaymentService.class).mockPay(order)
                    , 1, TimeUnit.SECONDS);
            if (order instanceof MainOrder) {
                MainOrder mainOrder = (MainOrder) order;
                mainOrder.setDisableSettlement(true);
                mainOrderRepository.save(mainOrder);
            }
            return payOrder;
        }
    }

    @Override
    public void orderMaintain() {

    }
}
