package cn.lmjia.cash.transfer;

import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.SupplierApiUpgradeException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 现金转账供应商
 *
 * @author CJ
 */
public interface CashTransferSupplier {

    /**
     * @param owner 在单业务主场景下，这个参数是可选的
     * @return 可用余额
     * @throws IOException                 网络异常
     * @throws SupplierApiUpgradeException 供应商API需更新
     * @throws BadAccessException          访问密钥错误
     */
    BigDecimal queryBalance(EntityOwner owner) throws IOException, SupplierApiUpgradeException, BadAccessException;

    /**
     * 现金转账功能
     * @param ownerAccount 主体账户信息
     * @param receiver 现金接受者
     * @throws IOException                 网络异常
     * @throws SupplierApiUpgradeException 供应商API需更新
     * @throws BadAccessException          访问密钥错误
     * @throws TransferFailureException     转账失败时异常
     * @return 可选的预计到账时间;有可能是null
     */
    LocalDateTime cashTransfer(OwnerAccount ownerAccount, CashReceiver receiver) throws IOException, SupplierApiUpgradeException, BadAccessException,TransferFailureException;
}
