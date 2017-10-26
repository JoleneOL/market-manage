package cn.lmjia.cash.transfer.cjb.message;

/**
 * 账户余额,交易流水查询
 */
public class ScustStmttrnRq {

    //TRNUID,客户端交易的唯一标志，至少应该保证在一次请求中该号唯一，否则客户端将无法分辨响应报文的对应关系,最大30位，建议值为YYYYMMDD+序号
    private String trnuId;

    //在响应报文中包含该内容
    private String cltcookie;

    //SCUSTSTMTRQ,生成转账付出指令内容
    private ScustStmtrq ScustStmtrq;
}
