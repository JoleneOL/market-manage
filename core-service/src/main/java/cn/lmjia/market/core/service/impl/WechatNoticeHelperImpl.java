package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.define.MarketNoticeType;
import cn.lmjia.market.core.define.MarketUserNoticeType;
import cn.lmjia.market.core.service.WechatNoticeHelper;
import cn.lmjia.market.core.util.AbstractTemplateMessageStyle;
import me.jiangcai.user.notice.wechat.WechatSendSupplier;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author CJ
 */
@Service
public class
WechatNoticeHelperImpl implements WechatNoticeHelper {

    @Autowired
    private Environment environment;
    @Autowired
    private WechatSendSupplier wechatSendSupplier;

    @Override
    public void registerTemplateMessage(MarketUserNoticeType type, String urlTemplate) {
        wechatSendSupplier.registerTemplateMessage(type, new AbstractTemplateMessageStyle() {
            @Override
            public Collection<? extends TemplateMessageParameter> parameterStyles() {
                return type.parameterStyles();
            }

            @Override
            public String getTemplateId() {
                return defaultTemplateId(type.type());
            }
        }, urlTemplate);
    }

    private String defaultTemplateId(MarketNoticeType type) {
        // 分别为3种环境提供不同的类型
        // 本地测试
        if (environment.acceptsProfiles(CoreConfig.ProfileUnitTest)) {
            switch (type) {
                case PaySuccessToOrder:
                    return "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY";
                case PaySuccessToJustOrder:
                    return "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY";
                case PaySuccessToCS:
                    return "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY";
                case NewLoginToLogin:
                    return "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY";
                case PromotionRequestPaySuccess:
                    return "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY";
                case TRJCheckWarning:
                    return "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY";
                case PromotionRequestRejected:
                    return "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY";
                default:
                    return "V7Tu9FsG9L-WFgdrMPtcnWl3kv15_iKfz_yIoCbjtxY";
            }
        }
        // staging
        if (environment.acceptsProfiles("staging")) {
            switch (type) {
                case PaySuccessToOrder:
                    return "X1gHrZDnxG6x4CHmqZSsEa1MYDfT9y1gSkYXTGy__nU";
                case PaySuccessToJustOrder:
                    return "X1gHrZDnxG6x4CHmqZSsEa1MYDfT9y1gSkYXTGy__nU";
                case PaySuccessToCS:
                    return "ZKMii3civ10Ig4-MnUDcZNGjyvLqYokVN7LlalN2l_g";
                case NewLoginToLogin:
                    return "1BW5Qk_xWsTvAPIGq6evAYUz2qwga4LMrgcvO3pm4X0";
                case PromotionRequestPaySuccess:
                    return "X1gHrZDnxG6x4CHmqZSsEa1MYDfT9y1gSkYXTGy__nU";
                case TRJCheckWarning:
                    return "sS9rypOSFZO2-aS9v4G5rH3PxbZD8r8g2ZdaOMaiRdg";
                case TeamMemberDeleteWarn:
                case TeamMemberDeleteNotify:
                    return "7I5cMlNFVAB1ku-P8FAmYU2CFCJ3aoWnU_yd-8LTnN0";
                default:
                    throw new IllegalArgumentException("未知的staging消息类型:" + type);
            }
        }

        // 正式
        switch (type) {
            case PaySuccessToOrder:
                return "ieAp4pLGQtEE9DZbbAP0_76xNrnjpoHNpQYe2DT8ID0";
            case PaySuccessToJustOrder:
                return "ieAp4pLGQtEE9DZbbAP0_76xNrnjpoHNpQYe2DT8ID0";
            case PaySuccessToCS:
                return "Ibbpm1SUpkPdiVcNSffv75PlbQzjY2753q3951YL2RM";
            case NewLoginToLogin:
                return "XBZMILcxV55ATb5Dkn_BWjfvzUT4ySRvsXskzzOuS14";
            case PromotionRequestPaySuccess:
                return "F-TTCFAOn9IhDO7a4-1ruwPNg6TjGpkxbVhdsLre0aE";
            case TRJCheckWarning:
                return "GXQS-UxMQDQD6cCMMNeoZ2fNHOq3Q7l6MXMD2hh_Ass";
            case TeamMemberDeleteNotify:
            case TeamMemberDeleteWarn:
//            {{first.DATA}}
//            绑定时间：{{keyword1.DATA}}
//            失败原因：{{keyword2.DATA}}
//            记录时间：{{keyword3.DATA}}
//            {{remark.DATA}}
                return "qmKucHYknwnTL_3wISnRkLjucfeixbIosd83k-1svMo";
            default:
                throw new IllegalArgumentException("未知的消息类型:" + type);
        }
    }
}
