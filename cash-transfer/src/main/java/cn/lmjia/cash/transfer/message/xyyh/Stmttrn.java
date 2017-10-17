package cn.lmjia.cash.transfer.message.xyyh;

import java.math.BigDecimal;

/**
 * 交易记录
 * @author lxf
 */
public class Stmttrn {

    //柜员流水号
    private String srvrtId;

    //交易类型 必回
    private String trnType;

    //核心摘要代码 必回
    private String trnCode;

    //记账日期 必回
    private String dtAcct;

    //交易金额
    private BigDecimal trnAmt;

    //余额 必回
    private BigDecimal balAmt;

    //币种
    private String currency;

    //“摘要简称|用途（来账，往账：用途。1187补录，基本为交易代码）”如果无用途，则只返回” 摘要简称” （非必回）
    private String memo;

    //对方账号（非必回）
    private String correlate_acctId;

    //对方账户名称 非必回
    private String correlate_name;

    //本行凭证代号 （非必回）；如果有回复，规则是2位凭证种类+7位凭证代号
    private String chequeNum;

    //他行凭证类型  2位（非必回）
    private String billType;

    //他行凭证号码  最大8位（非必回）
    private String billNumber;

    //附加行名  最大50位（非必回）
    private String correlate_bankName;

    //附加行号  12位（非必回）
    private String correlate_bankCode;

    //业务类型  最大20位（非必回）（非必回，摘要代号---业务部门提供）
    private String businessType;

    //流水唯一标识号，由流水交易日期、核心传票组序号、核心传票组内序号组成，最长30位
    private String attachInfo;

}
