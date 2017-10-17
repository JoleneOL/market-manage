package cn.lmjia.cash.transfer.message.xyyh;

/**
 * ACCTFROM,付款人账号
 * @author lxf
 */
public class Acctfrom {

    //付款账号  18位
    private String acctId;

    //付款人姓名,选填 最大50位
    private String name;

    //开户行,选填 仅在报文中体现
    private String bankDesc;

    //城市 ,选填 仅在报文中体现
    private String city;
}
