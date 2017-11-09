package cn.lmjia.cash.transfer;

import java.math.BigDecimal;

/**
 * 提现信息
 * @author lxf
 */
public interface CashReceiver {
    /**
     * @return 该提现申请数据库中id;
     */
    Long getId();

    /**
     * @return 获取该提现申请在于银行交接是转账流水号.如果为Null说明是没发送过转账指令.
     */
    String getWithdrawId();
    /**
     * @return 收款账号,最大32位
     */
    String getAccountNum();

    /**
     * @return 收款人姓名,最大50位
     */
    String getName();

    /**
     * @return 收款开户行名称 ,最大50位
     */
    String getBankDesc();

    /**
     * @return 收款人收报行号，12位
     */
    String getBankNumber();

    /**
     * @return 收款人城市,同城不必填写
     */
    String getCity();

    /**
     * @return 提现金额
     */
    BigDecimal getWithdrawAmount();

    /**
     * @return 用款用途
     */
    String getPurpose();


    /**
     * @return 备注
     */
    String getMemo();
}
