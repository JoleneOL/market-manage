package cn.lmjia.cash.transfer.cjb.message.sonrq;

import cn.lmjia.cash.transfer.cjb.message.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录请求与响应信息
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sonrq implements Serializable {
    private static final long serialVersionUID = -2791657036325365113L;

    /**
     * 客户端日期时间YYYY-MM-DD_HH:MM:SS 必输
     */
    @JsonProperty("DTCLIENT")
    private String dtClient;

    /**
     * 企业网银客户号，10位数字字符 必输
     */
    @JsonProperty("CID")
    private String cid;

    /**
     * 登录用户名，最长：20位 必输
     */
    @JsonProperty("USERID")
    private String userId;

    /**
     * 登录密码，最长：30位 必输
     */
    @JsonProperty("USERPASS")
    private String userPass;

    /**
     * USERKEY与(USERID和USERPASS)不同时出现，由服务器在上一次请求响应中提供，建议使用(USERID和USERPASS) 必输
     */
    @JsonProperty("USERKEY")
    private String userKey;

    /**
     * 是否需要服务器产生USERKEY,，填Y、N 必输
     */
    @JsonProperty("GENUSERKEY")
    private String genUserKey;

    /**
     * 希望服务器响应信息使用的语言，目前仅支持CHS(中文简体) ,非必输
     */
    @JsonProperty("LANGUAGE")
    private String language;

    /**
     * 客户端应用程序编码，五个字符 ,非必输
     */
    @JsonProperty("APPID")
    private String appId;

    /**
     * 客户端应用程序版本nnnn ,非必输
     */
    @JsonProperty("APPVER")
    private String appVer;


}
