package cn.lmjia.market.core.define;

import cn.lmjia.market.core.service.request.PromotionRequestService;

/**
 * 本项目中所有通知的种类
 *
 * @author CJ
 */
public enum MarketNoticeType {
    PaySuccessToOrder, PaySuccessToJustOrder, NewLoginToLogin, PaySuccessToCS, TRJCheckWarning,
    /**
     * 升级支付成功
     */
    PromotionRequestPaySuccess(PromotionRequestService.class.getSimpleName() + ".PaySuccess"),
    /**
     * 成员删除警告
     */
    TeamMemberDeleteWarn,
    /**
     * 成员删除通知
     */
    TeamMemberDeleteNotify,
    /**
     * 拒绝升级申请
     */
    PromotionRequestRejected,
    /**
     * 通知财务客户佣金提现申请
     */
    WithdrawSuccessRemindFinancial;


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
