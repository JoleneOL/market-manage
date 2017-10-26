package cn.lmjia.cash.transfer.cjb.message;

/**
 * 0-n， 对应请求的响应，可能包括同步的内容
 */
public class ScustStmttrnRs {

    //客户端交易的唯一标志 必回
    private String trnnId;

    //状态码 必回
    private Status status;

    //如果客户端发送COOKIE，同步的历史记录不包括原有的CLTCOOKIE
    private String cltCookie;

    private ScustStmtrs scustStmtrs;
}
