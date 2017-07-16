package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.support.PaymentStatus;
import cn.lmjia.market.core.entity.support.PromotionRequestStatus;
import cn.lmjia.market.core.repository.request.PromotionRequestRepository;
import cn.lmjia.market.core.service.request.PromotionRequestService;
import cn.lmjia.market.core.util.AbstractTemplateMessageStyle;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.seext.FileUtils;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.user.notice.UserNoticeType;
import me.jiangcai.user.notice.wechat.WechatSendSupplier;
import me.jiangcai.wx.model.message.SimpleTemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * @author CJ
 */
@Service
public class PromotionRequestServiceImpl implements PromotionRequestService {

    @Autowired
    private PromotionRequestRepository promotionRequestRepository;
    @Autowired
    private Environment environment;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private ResourceService resourceService;

    private boolean useLocal() {
        return environment.acceptsProfiles("staging") || environment.acceptsProfiles(CoreConfig.ProfileUnitTest);
    }

    @Override
    public PromotionRequest currentRequest(Login login) {
        return promotionRequestRepository.findByWhoseAndRequestStatusOrderByIdDesc(login, PromotionRequestStatus.requested)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public PromotionRequest initRequest(Login login, String agentName, int type, Address address, String cardBackPath
            , String cardFrontPath, String businessLicensePath) throws IOException {
        // 初始化配置，并且进行转存
        PromotionRequest request = new PromotionRequest();
        request.setPaymentStatus(PaymentStatus.wait);
        request.setName(agentName);
        request.setAddress(address);
        request.setPrice(getPriceFor1());
        request.setRequestStatus(PromotionRequestStatus.init);
        request.setRequestTime(LocalDateTime.now());
        request.setType(type);
        request.setWhose(login);
        // 转存资源
        request = promotionRequestRepository.saveAndFlush(request);

        String cardBackResource = "promotionRequest/" + request.getId() + "/back." + FileUtils.fileExtensionName(cardBackPath);
        String cardFrontResource = "promotionRequest/" + request.getId() + "/front." + FileUtils.fileExtensionName(cardFrontPath);
        String businessLicenseResource;
        if (!StringUtils.isEmpty(businessLicensePath)) {
            businessLicenseResource = "promotionRequest/" + request.getId() + "/businessLicense." + FileUtils.fileExtensionName(businessLicensePath);
            resourceService.moveResource(businessLicenseResource, businessLicensePath);
        } else {
            businessLicenseResource = null;
        }
        resourceService.moveResource(cardBackResource, cardBackPath);
        resourceService.moveResource(cardFrontResource, cardFrontPath);

        request.setBackImagePath(cardBackResource);
        request.setFrontImagePath(cardFrontResource);
        request.setBusinessLicensePath(businessLicenseResource);

        return request;
    }

    @Override
    public void submitRequest(PromotionRequest request) {
        request.setRequestStatus(PromotionRequestStatus.requested);
        request.setChangeTime(LocalDateTime.now());
        promotionRequestRepository.save(request);
    }

    @Override
    public UserNoticeType getPaySuccessMessage() {
        return new UserNoticeType() {
            @Override
            public String id() {
                return PromotionRequestService.class.getSimpleName() + ".PaySuccess";
            }

            @Override
            public String title() {
                return "升级经销商支付成功";
            }

            @Override
            public boolean allowDifferentiation() {
                return true;
            }

            @Override
            public String defaultToText(Locale locale, Object[] parameters) {
                return "您的订单已成功支付。";
            }

            @Override
            public String defaultToHTML(Locale locale, Object[] parameters) {
                return "您的订单已成功支付。";
            }

            @Override
            public Class<?>[] expectedParameterTypes() {
                return new Class<?>[]{
                        String.class
                        , Long.class
                        , BigDecimal.class
                        , Date.class
                };
            }
        };
    }

    @Override
    public void registerNotices(WechatSendSupplier wechatSendSupplier) {
        wechatSendSupplier.registerTemplateMessage(getPaySuccessMessage(), new AbstractTemplateMessageStyle() {
            @Override
            public Collection<? extends TemplateMessageParameter> parameterStyles() {
                return Arrays.asList(
                        new SimpleTemplateMessageParameter("first", "{0}，您好，您的经销商升级订单已支付。")
                        , new SimpleTemplateMessageParameter("keyword1", "{1}")
                        , new SimpleTemplateMessageParameter("keyword2", "{2,number,￥,###.##}")
                        , new SimpleTemplateMessageParameter("keyword3", "{3}")
                        , new SimpleTemplateMessageParameter("keyword4", "微信支付")
                        , new SimpleTemplateMessageParameter("remark", "请耐心等待审核。")
                );
            }

            @Override
            public String getTemplateId() {
                return useLocal() ? "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY" : "F-TTCFAOn9IhDO7a4-1ruwPNg6TjGpkxbVhdsLre0aE";
            }
        }, null);
    }

    @Override
    public BigDecimal getPriceFor1() {
        return systemStringService.getCustomSystemString("market.price.promotion.agent1"
                , null, true, BigDecimal.class, new BigDecimal("30000"));
    }
}
