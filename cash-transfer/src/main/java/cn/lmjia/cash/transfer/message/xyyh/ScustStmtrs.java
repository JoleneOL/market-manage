package cn.lmjia.cash.transfer.message.xyyh;

/**
 *  转账付出指令内容
 * @author lxf
 */
public class ScustStmtrs {

    //默认货币代码 必回
    private String curdef;

    //付款人账户
    private Acctfrom acctfrom;

    //账户交易明细,非必回，有明细信息才有返回
    private TranList tranList;

    //总账余额， 必回
    private LedgerBal ledgerBal;

    //非必回
    private AvailBal availBal;
}
