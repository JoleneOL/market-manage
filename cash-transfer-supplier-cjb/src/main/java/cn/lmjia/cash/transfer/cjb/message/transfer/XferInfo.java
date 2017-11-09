package cn.lmjia.cash.transfer.cjb.message.transfer;

import cn.lmjia.cash.transfer.cjb.message.AcctTo;
import cn.lmjia.cash.transfer.cjb.message.Acctfrom;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 转账信息
 * @author lxf
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XferInfo implements Serializable{
    private static final long serialVersionUID = -9141284583424900474L;

    /**
     * 付款人账户信息
     */
    @JsonProperty("ACCTFROM")
    private Acctfrom acctfrom;

    /**
     * 收款人账户信息
     */
    @JsonProperty("ACCTTO")
    private AcctTo acctTo;

    /**
     * 凭证号，7位数字；可不填，默认使用电子凭证号
     */
    @JsonProperty("CHEQUENUM")
    private String chequeNum;

    /**
     * 货币符号,RMB ，如有该节点，请填入“RMB”,非必输,目前只支持rmb
     */
    @JsonProperty("CURSYM")
    private String cursym;

    /**
     * 转账金额，不能为空、空格且金额必须大于0.01，必须为数值型，decimal(17,2)，即整数位最长15位，小数位2位，以下同,必输
     */
    @JsonProperty("TRNAMT")
    private String trnAmt;

    /**
     * 支付方式 ,非输入
     */
    @JsonProperty("PMTMODE")
    private String pmtMode;

    /**
     * 用款用途，最大30位，不允许为空或空格,必输入
     */
    @JsonProperty("PURPOSE")
    private String purPose;

    /**
     * 客户端要求的转账执行日期，如果客户端未发送DTDUE，则服务器将尽可能早执行转账。格式：YYYY-MM-DD 非必输。预约期限最长不超过15天.
     */
    @JsonProperty("DTDUE")
    private String dtDue;

    /**
     * 备注，可选，最大 60位,非必输
     */
    @JsonProperty("MEMO")
    private String memo;
}
