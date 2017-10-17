package cn.lmjia.cash.transfer.message.xyyh;

/**
 * 兴业银行报文根对象
 * @author lxf
 */
public class Fox {
    //登录请求信息
    private SignonMsgSRQV1 signonMsgSRQV1;

    //登录响应信息
    private SignonMsgsRSV1 signonMsgsRSV1;

    //余额与交易流水查询
    private Securities_msgsRQV1 securities_msgsRQV1;

    //余额与交易流水相应
    private Securities_msgsRSV1 securities_msgsRSV1;

    public SignonMsgSRQV1 getSignonMsgSRQV1(){
        return signonMsgSRQV1;
    }

    public void setSignonMsgSRQV1(SignonMsgSRQV1 signonMsgSRQV1) {
        this.signonMsgSRQV1 = signonMsgSRQV1;
    }
}
