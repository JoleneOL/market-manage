package cn.lmjia.cash.transfer.message.xyyh;

import java.util.Date;

/**
 * 登录请求与响应信息
 * @author lxf
 */
public class Sonrq {
    //客户端日期时间YYYY-MM-DD_HH:MM:SS 必输
    private Date dtClient;

    //企业网银客户号，10位数字字符 必输
    private String cId;

    //登录用户名，最长：20位 必输
    private String userId;

    //登录密码，最长：30位 必输
    private String userPass;

    //USERKEY与(USERID和USERPASS)不同时出现，由服务器在上一次请求响应中提供，建议使用(USERID和USERPASS) 必输
    private String userKey;

    //是否需要服务器产生USERKEY,，填Y、N 必输
    private String genUserKey;

    //希望服务器响应信息使用的语言，目前仅支持CHS(中文简体) ,非必输
    private String language;

    //客户端应用程序编码，五个字符 ,非必输
    private String appId;

    //客户端应用程序版本nnnn ,非必输
    private String appVer;

    //下面登陆响应

    //交易处理状态 必回
    private Status status;

    //服务端日期时间，YYYY-MM-DD HH:MM:SS 必回
    private Date dtServer;

    //UserKey的有效时间 服务器时间 ,非必回，仅在GENUSERKEY为”Y”时必回
    private String tsKeyExpire;

    //服务器需要保存会话COOKIE，则发送，否则不发送，客户端在下次请求中应包含 ,非必回
    private String sessCookie;

}
