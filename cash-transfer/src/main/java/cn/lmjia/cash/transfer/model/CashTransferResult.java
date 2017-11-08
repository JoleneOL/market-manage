package cn.lmjia.cash.transfer.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CashTransferResult {

    /**
     * 客户端流水号,查询时是 查询指令的流水号, 转账时是转账指令的流水号.
     */
    private String clientSerial;

    /**
     * 服务端流水号
     */
    private String serviceSerial;

    /**
     * 备注
     */
    private String memo;

    /**
     * 转账指令结果状态码
     */
    private String resultStatuCode;


    /**
     * 转账指令的处理时间.
     */
    private LocalDateTime processingTime;

    /**
     * 转账指令的处理信息.
     */
    private String message;

}
