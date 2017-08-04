package cn.lmjia.market.core.trj.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.entity.trj.AuthorisingInfo;
import cn.lmjia.market.core.entity.trj.AuthorisingStatus;
import cn.lmjia.market.core.entity.trj.TRJPayOrder;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.repository.trj.AuthorisingInfoRepository;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.ManagerService;
import cn.lmjia.market.core.service.NoticeService;
import cn.lmjia.market.core.service.ScriptTaskService;
import cn.lmjia.market.core.trj.InvalidAuthorisingException;
import cn.lmjia.market.core.trj.TRJService;
import cn.lmjia.market.core.util.AbstractTemplateMessageStyle;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.event.OrderPaySuccess;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.service.PaymentService;
import me.jiangcai.user.notice.UserNoticeService;
import me.jiangcai.user.notice.UserNoticeType;
import me.jiangcai.user.notice.wechat.WechatSendSupplier;
import me.jiangcai.wx.model.message.SimpleTemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author CJ
 */
@Service
public class TRJServiceImpl implements TRJService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Log log = LogFactory.getLog(TRJServiceImpl.class);
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private final String urlRoot;
    private final String tenant;
    private final String key;
    private final String autoRecallCode;
    @Autowired
    private AuthorisingInfoRepository authorisingInfoRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @Autowired
    private ScriptTaskService scriptTaskService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserNoticeService userNoticeService;
    @Autowired
    private WechatSendSupplier wechatSendSupplier;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private ResourceService resourceService;

    @Autowired
    public TRJServiceImpl(Environment environment) throws IOException {
        urlRoot = environment.getProperty("me.jiangcai.dating.tourongjia.url2", "http://crmtest2.tourongjia.com");
        tenant = environment.getProperty("me.jiangcai.dating.tourongjia.tenant", "yuntao");
        key = environment.getProperty("me.jiangcai.dating.tourongjia.key", "1234567890");
        autoRecallCode = StreamUtils.copyToString(new ClassPathResource("/script/auto-recall.js").getInputStream(), Charset.forName("UTF-8"));
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

        log.trace("排序并且串联后：" + sb.toString());

        sb.append(secretKey);

        log.trace("加入Key：" + sb.toString());

        final String hex = DigestUtils.md5Hex(sb.toString());
        log.trace("MD5 & Hex:" + hex);
        return hex;
    }

    @Override
    public void addAuthorisingInfo(String authorising, String idNumber) {
        if (authorisingInfoRepository.findOne(authorising) != null)
            throw new IllegalArgumentException(authorising + "existing");
        AuthorisingInfo info = new AuthorisingInfo();
        info.setId(authorising);
        info.setIdNumber(idNumber);
        info.setCreatedTime(LocalDateTime.now());
        authorisingInfoRepository.save(info);
    }

    @Override
    public void deliverUpdate(long orderId, String deliverCompany, String deliverStore, int stockQuantity
            , LocalDate shipmentTime, LocalDate deliverTime) {
        MainOrder order = mainOrderRepository.getOne(orderId);
        TRJPayOrder payOrder = (TRJPayOrder) order.getPayOrder();
        try {
            deliverUpdate(orderId, payOrder.getAuthorisingInfo().getId(), deliverCompany, deliverStore, stockQuantity
                    , shipmentTime.format(dateFormatter), deliverTime.format(dateFormatter), order.getCustomer().getName()
                    , order.getCustomer().getMobile(), order.getInstallAddress().toTRJString()
                    , order.getOrderTime().format(formatter));
        } catch (Exception e) {
            log.debug("[TRJ]", e);
            String code = String.format("context.getBean(Packages.cn.lmjia.market.core.trj.TRJService.class).deliverUpdate(" +
                            "%d,\"%s\",\"%s\",\"%s\",%d" +
                            ",\"%s\",\"%s\",\"%s\"" +
                            ",\"%s\",\"%s\"" +
                            ",\"%s\")"
                    , orderId, payOrder.getAuthorisingInfo().getId(), deliverCompany, deliverStore, stockQuantity
                    , shipmentTime.format(dateFormatter), deliverTime.format(dateFormatter), order.getCustomer().getName()
                    , order.getCustomer().getMobile(), order.getInstallAddress().toTRJString()
                    , order.getOrderTime().format(formatter));

            submitTask("提交物流信息", code);
        }
    }

    @Override
    public void submitOrderCompleteRequest(MainOrder order, String installer, String installCompany, String mobile
            , LocalDateTime installTime, String resourcePath) {
        TRJPayOrder payOrder = (TRJPayOrder) order.getPayOrder();
        try {
            submitOrderCompleteRequest(payOrder.getAuthorisingInfo().getId(), order.getId()
                    , order.getInstallAddress().toTRJString(), installer, installCompany, mobile
                    , installTime.format(formatter), order.getAmount(), resourcePath);
        } catch (Exception e) {
            log.debug("[TRJ]", e);
            String code;
            if (org.springframework.util.StringUtils.isEmpty(resourcePath))
                code = String.format("context.getBean(Packages.cn.lmjia.market.core.trj.TRJService.class).submitOrderCompleteRequest(" +
                                "\"%s\",%d" +
                                ",\"%s\",\"%s\",\"%s\",\"%s\"" +
                                ",\"%s\",%d,null)"
                        , payOrder.getAuthorisingInfo().getId(), order.getId()
                        , order.getInstallAddress().toTRJString(), installer, installCompany, mobile
                        , installTime.format(formatter), order.getAmount());
            else
                code = String.format("context.getBean(Packages.cn.lmjia.market.core.trj.TRJService.class).submitOrderCompleteRequest(" +
                                "\"%s\",%d" +
                                ",\"%s\",\"%s\",\"%s\",\"%s\"" +
                                ",\"%s\",%d,\"%s\")"
                        , payOrder.getAuthorisingInfo().getId(), order.getId()
                        , order.getInstallAddress().toTRJString(), installer, installCompany, mobile
                        , installTime.format(formatter), order.getAmount(), resourcePath);

            submitTask("提交信审请求", code);
        }
    }

    @Override
    public MainOrder findOrder(String authorising) {
        return mainOrderRepository.findOne((root, query, cb) -> cb.and(
                cb.equal(root.get("payOrder").type(), TRJPayOrder.class)
                , cb.equal(cb.treat(root.join("payOrder"), TRJPayOrder.class).get("authorisingInfo").get("id")
                        , authorising)
        ));
    }

    @Override
    public void orderSuccess(MainOrderFinishEvent event) {
        final MainOrder mainOrder = event.getMainOrder();
        if (mainOrder.getPayOrder() instanceof TRJPayOrder) {
            TRJPayOrder payOrder = (TRJPayOrder) mainOrder.getPayOrder();
            payOrder.getAuthorisingInfo().setAuthorisingStatus(AuthorisingStatus.forAuditing);
            sendCheckWarningToCS(mainOrder, "订单已完成，可以申请信审了。");
        }
    }

    @Override
    public void sendCheckWarningToCS(MainOrder order, String message) {
        userNoticeService.sendMessage(null, loginService.toWechatUser(managerService.levelAs(ManageLevel.customerService))
                , null, new TRJCheckWarning(), message, order.getSerialId()
                , Date.from(ZonedDateTime.of(order.getOrderTime(), ZoneId.systemDefault()).toInstant()), order.getInstallAddress().toString());
    }

    @PostConstruct
    @Autowired
    public void init() {
        wechatSendSupplier.registerTemplateMessage(new TRJCheckWarning(), new AbstractTemplateMessageStyle() {
            @Override
            public String getTemplateId() {
                return noticeService.useLocal() ? "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY" : "GXQS-UxMQDQD6cCMMNeoZ2fNHOq3Q7l6MXMD2hh_Ass";
            }

            @Override
            public Collection<? extends TemplateMessageParameter> parameterStyles() {
                return Arrays.asList(
                        new SimpleTemplateMessageParameter("first", "{0}")
                        , new SimpleTemplateMessageParameter("keyword1", "{1}")
                        , new SimpleTemplateMessageParameter("keyword2", "已安装")
                        , new SimpleTemplateMessageParameter("keyword3", "{2,date,yyyy-MM-dd HH:mm}")
                        , new SimpleTemplateMessageParameter("keyword4", "{3}")
                        , new SimpleTemplateMessageParameter("remark", "请尽快发送或者重新发送信审申请。")
                );
            }
        }, null);
    }

    private void submitOrderInfo(MainOrder order, AuthorisingInfo info) {
        // 使用脚本运作
        final Login guideUser = order.getOrderBy().getGuideUser();
        Long recommendId;
        if (guideUser != null)
            recommendId = guideUser.getId();
        else
            recommendId = 0L;
        try {
            submitOrderInfo(info.getId(), order.getId(), order.getCustomer().getName(), info.getIdNumber()
                    , order.getCustomer().getMobile(), order.getGood().getProduct().getCode()
                    , order.getGood().getProduct().getName(), order.getAmount()
                    , order.getOrderDueAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString()
                    , order.getInstallAddress().toTRJString(), order.getOrderTime().format(formatter)
                    , recommendId);
        } catch (Exception e) {
            // 提交任务
            log.debug("[TRJ]", e);
            String code = String.format("context.getBean(Packages.cn.lmjia.market.core.trj.TRJService.class).submitOrderInfo(" +
                            "\"%s\",%d,\"%s\",\"%s\"" +
                            ",\"%s\",\"%s\"" +
                            ",\"%s\",%d" +
                            ",\"%s\"" +
                            ",\"%s\",\"%s\",%d)"
                    , info.getId(), order.getId(), order.getCustomer().getName(), info.getIdNumber()
                    , order.getCustomer().getMobile(), order.getGood().getProduct().getCode()
                    , order.getGood().getProduct().getName(), order.getAmount()
                    , order.getOrderDueAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString()
                    , order.getInstallAddress().toTRJString(), order.getOrderTime().format(formatter)
                    , recommendId);

            submitTask("提交订单信息", code);
        }
    }

    @Override
    public void submitOrderCompleteRequest(String authorising, Number orderId, String address, String installer
            , String installCompany, String mobile, String installTime, Number amount, String resourcePath) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            Function<List<NameValuePair>, HttpEntity> entity;
            if (!org.springframework.util.StringUtils.isEmpty(resourcePath)) {
                Resource resource = resourceService.getResource(resourcePath);
                if (resource.exists()) {
                    final String fileName = resourcePath.substring(resourcePath.lastIndexOf("/") + 1);
                    final byte[] data = StreamUtils.copyToByteArray(resource.getInputStream());
                    entity = nameValuePairs -> {
                        final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
//                                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                                .addBinaryBody("attach", data
                                        , ContentType.parse(new MimetypesFileTypeMap().getContentType(resourcePath))
                                        , fileName);
                        nameValuePairs.forEach(nameValuePair
                                -> entityBuilder.addTextBody(nameValuePair.getName(), nameValuePair.getValue()
                                , ContentType.create("text/plain", Charset.forName("UTF-8"))));
                        return entityBuilder.build();
                    };
                } else
                    entity = null;
            } else
                entity = null;
            client.execute(newUriRequest("/tenant/goods_orderInstallationInfo.jhtml"
                    , entity
                    , new BasicNameValuePair("authorising", authorising)
                    , new BasicNameValuePair("orderId", String.valueOf(orderId))
                    , new BasicNameValuePair("installationInfo.installAddress", address)
                    , new BasicNameValuePair("installationInfo.installPerson", installer)
                    , new BasicNameValuePair("installationInfo.companyName", installCompany)
                    , new BasicNameValuePair("installationInfo.mobile", mobile)
                    , new BasicNameValuePair("installationInfo.installTime", installTime)
                    , new BasicNameValuePair("installationInfo.installationQuanity", String.valueOf(amount))
            ), new StrangeJsonHandler<>(Void.class));
        }
        MainOrder order = mainOrderRepository.getOne(orderId.longValue());
        TRJPayOrder payOrder = (TRJPayOrder) order.getPayOrder();
        final AuthorisingInfo authorisingInfo = payOrder.getAuthorisingInfo();
        authorisingInfo.setAuthorisingStatus(AuthorisingStatus.auditing);
        authorisingInfoRepository.save(authorisingInfo);
    }

    @Override
    public void deliverUpdate(Number orderId, String authorising, String deliverCompany, String deliverStore
            , Number stockQuantity, String shipmentTime, String deliverTime, String name, String mobile, String address
            , String orderTime) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            client.execute(newUriRequest("/tenant/goods_orderShipmentInfo.jhtml"
                    , new BasicNameValuePair("authorising", authorising)
                    , new BasicNameValuePair("orderId", String.valueOf(orderId))
                    , new BasicNameValuePair("shipmentInfo.delieverCompany", deliverCompany)
                    , new BasicNameValuePair("shipmentInfo.delieverStore", deliverStore)
                    , new BasicNameValuePair("shipmentInfo.stockQuantity", String.valueOf(stockQuantity))
                    , new BasicNameValuePair("shipmentInfo.shipmentTime", shipmentTime)
                    , new BasicNameValuePair("shipmentInfo.delieveredTime", deliverTime)
                    , new BasicNameValuePair("shipmentInfo.recipients", name)
                    , new BasicNameValuePair("shipmentInfo.mobile", mobile)
                    , new BasicNameValuePair("shipmentInfo.address", address)
                    , new BasicNameValuePair("shipmentInfo.orderTime", orderTime)
            ), new StrangeJsonHandler<>(Void.class));
        }
    }

    @Override
    public void submitOrderInfo(String authorising, Number orderId, String name, String idNumber, String mobile
            , String goodCode, String goodName, Number amount, String dueAmount, String address, String orderTime
            , Number recommendCode) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            client.execute(newUriRequest("/tenant/goods_authorisingOrderInfo.jhtml"
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

    private HttpUriRequest newUriRequest(String uri, Function<List<NameValuePair>, HttpEntity> toEntity, NameValuePair... pairs) {
        List<NameValuePair> list = new ArrayList<>();
        list.addAll(Arrays.asList(pairs));
        list.add(new BasicNameValuePair("sign", sign(list)));
        // 串接
        @SuppressWarnings("StringBufferReplaceableByString")
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(urlRoot).append(uri);
//                .append("?");
//        list.forEach(nameValuePair -> {
//            try {
//                urlBuilder.append(nameValuePair.getName()).append("=").append(URLEncoder.encode(nameValuePair.getValue(), "UTF-8"))
//                        .append("&");
//            } catch (UnsupportedEncodingException e) {
//                throw new InternalError(e);
//            }
//        });
//
//        urlBuilder.setLength(urlBuilder.length() - 1);
//        if (log.isDebugEnabled())
//            log.debug("[TRJ]" + urlBuilder.toString());

        if (toEntity == null) {
            toEntity = nameValuePairs -> EntityBuilder.create()
                    .setContentType(ContentType.APPLICATION_FORM_URLENCODED.withCharset("UTF-8"))
                    .setParameters(nameValuePairs)
                    .build();
        }

        HttpPost post = new HttpPost(urlBuilder.toString());
        post.setEntity(toEntity.apply(list));
        return post;
    }

    @Override
    public String sign(List<NameValuePair> list) {
        list.add(new BasicNameValuePair("tenant", tenant));
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
            info.setAuthorisingStatus(AuthorisingStatus.forOrderComplete);
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

    private void submitTask(String name, String code) {
        scriptTaskService.submitTask(name, Instant.now().plusSeconds(30), code, null
                , autoRecallCode);
    }

    private class TRJCheckWarning implements UserNoticeType {

        @Override
        public String id() {
            return "TRJCheckWarning";
        }

        @Override
        public String title() {
            return "需要信审的客服通知";
        }

        @Override
        public boolean allowDifferentiation() {
            return true;
        }

        @Override
        public String defaultToText(Locale locale, Object[] parameters) {
            return "需要信审的客服通知";
        }

        @Override
        public String defaultToHTML(Locale locale, Object[] parameters) {
            return "需要信审的客服通知";
        }

        @Override
        public Class<?>[] expectedParameterTypes() {
            return new Class<?>[]{
                    //message
                    String.class
                    //订单号
                    , String.class
                    , Date.class
                    //地址
                    , String.class
            };
        }
    }
}
