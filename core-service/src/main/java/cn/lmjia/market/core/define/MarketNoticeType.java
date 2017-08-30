package cn.lmjia.market.core.define;

import cn.lmjia.market.core.service.request.PromotionRequestService;

/**
 * 本项目中所有通知的种类
 *
 * @author CJ
 */
public enum MarketNoticeType {
    PaySuccessToOrder, PaySuccessToJustOrder, NewLoginToLogin, PaySuccessToCS, TRJCheckWarning, PromotionRequestPaySuccess(PromotionRequestService.class.getSimpleName() + ".PaySuccess");

    private final String targetId;

    MarketNoticeType() {
        this(null);
    }

    MarketNoticeType(String targetId) {
        this.targetId = targetId;
    }

    /**
     * @return 语义等同{@link me.jiangcai.user.notice.UserNoticeType#id()}
     */
    public String getUserNoticeTypeId() {
        if (targetId != null)
            return targetId;
        return name();
    }
}
