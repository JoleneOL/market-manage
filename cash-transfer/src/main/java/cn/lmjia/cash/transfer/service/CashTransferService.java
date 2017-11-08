package cn.lmjia.cash.transfer.service;

import cn.lmjia.cash.transfer.CashReceiver;
import cn.lmjia.cash.transfer.CashTransferSupplier;
import cn.lmjia.cash.transfer.EntityOwner;
import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.SupplierApiUpgradeException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;

import java.io.IOException;
import java.util.Map;

/**
 * 现金转账服务
 * @author lxf
 */
public interface CashTransferService {
    /**
     * 转账服务
     * @param supplier 供应商,可以为Null默认是兴业银行
     * @param owner 主体;在单主体的环境中可以为null
     * @param cashReceiver 现金接受者信息
     * @return 请求处理结果
     */
    Map<String, Object> cashTransfer(CashTransferSupplier supplier, EntityOwner owner,
                                     CashReceiver cashReceiver) throws SupplierApiUpgradeException, BadAccessException
            , TransferFailureException, IOException;
}
