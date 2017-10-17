package cn.lmjia.cash.transfer.message.xyyh;

/**
 *  总账余额
 * @author lxf
 */
public class LedgerBal {

    //活期账户余额，和下面可用余额基本一致，除非存在贷款户等业务上冻结或被控制的金额，总账才会比可用大。
    private String balAmt;

    //日期
    private String dtAsof;
}
