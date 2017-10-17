package cn.lmjia.cash.transfer.message.xyyh;

import java.util.Date;
import java.util.List;

/**
 * 账户交易明细Y-还有下页流水，N-无下页流水。
 * @author lxf
 */
public class TranList {

    //起始日期，必回
    private Date dtStart;

    //结束日期, 必回
    private Date dtEnd;

    //交易记录
    private List<Stmttrn> stmttrn;

}
