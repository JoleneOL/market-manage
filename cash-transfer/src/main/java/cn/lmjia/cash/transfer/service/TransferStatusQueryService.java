package cn.lmjia.cash.transfer.service;

import cn.lmjia.cash.transfer.CashReceiver;
import cn.lmjia.cash.transfer.CashTransferSupplier;
import cn.lmjia.cash.transfer.EntityOwner;
import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;
import cn.lmjia.cash.transfer.model.CashTransferResult;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 转账状态查询
 * @author lxf
 */
public interface TransferStatusQueryService {

    /**
     *
     * @param owner 业务主
     * @param supplier 经销商
     * @param receuver  需要查询的 提现指令 状态
     * @return 信息结果集
     * @throws TransferFailureException 转账失败时抛出,一般是账户信息错误
     * @throws BadAccessException 无法访问银行时抛出
     * @throws JsonProcessingException xml转对象是错误抛出
     */
    CashTransferResult statusQuery(EntityOwner owner, CashTransferSupplier supplier, CashReceiver receuver) throws TransferFailureException, BadAccessException,JsonProcessingException;
}
