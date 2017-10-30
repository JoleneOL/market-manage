package cn.lmjia.cash.transfer.service;

import cn.lmjia.cash.transfer.CashReceiver;
import cn.lmjia.cash.transfer.CashTransferSupplier;
import cn.lmjia.cash.transfer.EntityOwner;
import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

/**
 * 转账状态查询
 * @author lxf
 */
public interface TransferStatusQueryService {

    /**
     * @param owner 业务主
     * @param supplier 经销商
     * @param fBank 主体业务选择的银行
     * @param receuver  需要查询的 提现指令 状态
     * @return 信息结果集
     */
    Map<String, Object> statusQuery(EntityOwner owner , CashTransferSupplier supplier,String fBank, CashReceiver receuver) throws TransferFailureException, BadAccessException, JsonProcessingException;
}
