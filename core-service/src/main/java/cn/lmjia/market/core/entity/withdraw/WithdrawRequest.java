package cn.lmjia.market.core.entity.withdraw;

import cn.lmjia.cash.transfer.CashReceiver;
import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现申请
 */
@Entity
@Setter
@Getter
public class WithdrawRequest implements CashReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发起人
     */
    @ManyToOne
    private Login whose;

    /**
     * 收款人姓名
     */
    @Column(length = 20)
    private String payee;

    /**
     * 收款账号
     */
    @Column(length = 20)
    private String account;

    /**
     * 开户行
     */
    @Column(length = 20)
    private String bank;

    /**
     * 开户行所在城市
     */
    @Column(length = 20)
    private String bankCity;
    /**
     * 收款人电话
     */
    @Column(length = 20)
    private String mobile;

    /**
     * 提现金额
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal amount;
    /**
     * 转账金额
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal actualAmount;

    /**
     * 提现申请时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime requestTime;

    /**
     * 提现的状态
     */
    private WithdrawStatus withdrawStatus;

    /**
     * 是否提供发票
     */
    private boolean invoice;

    @Column(length = 20)
    private String logisticsCode;
    @Column(length = 20)
    private String logisticsCompany;

    /**
     * 转账单据编号 目前没有用
     */
//    @Column(length = 50)
//    private String transactionRecordNumber;

    /**
     * 备注信息，最多100个字
     */
    @Column(length = 100)
    private String comment;
    /**
     * 处理时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime manageTime;
    /**
     * 处理人
     */
    @ManyToOne
    private Manager manageBy;

    /**
     * 该申请在发送指令的客户端流水号.
     */
    @Column(length = 30)
    private String clientSerial;

    /**
     * 该请求在发送指令时,银行返回的服务端流水号.
     */
    @Column(length = 30)
    private String serviceSerial;
    /**
     * 银行处理时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime bankProcessingTime;

    /**
     * 提现目的
     */
    @Column(length = 30)
    private String withdrawPurpose;

    public Money getActualAmountMoney() {
        return new Money(actualAmount);
    }

    public Money getAmountMoney() {
        return new Money(amount);
    }

    public Money getTaxFee() {
        return new Money(amount.subtract(actualAmount));
    }

    @Override
    public String getWithdrawId() {
        return this.clientSerial;
    }

    @Override
    public String getAccountNum() {
        return this.account;
    }

    @Override
    public String getName() {
        return this.payee;
    }

    @Override
    public String getBankDesc() {
        return this.bank;
    }

    @Override
    public String getBankNumber() {
        //可以不用填写,先留着,万一将来需要呢
        return null;
    }

    @Override
    public String getCity() {
        return this.bankCity;
    }

    @Override
    public BigDecimal getWithdrawAmount() {
        return this.amount;
    }

    @Override
    //目前只有佣金提现,还有别的吗?
    public String getPurpose() {
        return withdrawPurpose == null?"佣金提现":withdrawPurpose;
    }

    @Override
    public String getMemo() {
        return this.comment;
    }
}
