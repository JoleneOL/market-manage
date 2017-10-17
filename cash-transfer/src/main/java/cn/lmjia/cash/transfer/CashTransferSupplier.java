package cn.lmjia.cash.transfer;

import java.io.IOException;
import java.math.BigDecimal;

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

}
