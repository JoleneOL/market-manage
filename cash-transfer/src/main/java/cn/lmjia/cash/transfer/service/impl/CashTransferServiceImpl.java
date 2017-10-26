package cn.lmjia.cash.transfer.service.impl;

import cn.lmjia.cash.transfer.*;
import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.SupplierApiUpgradeException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;
import cn.lmjia.cash.transfer.service.CashTransferService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CashTransferServiceImpl implements CashTransferService {

    private static final Log log = LogFactory.getLog(CashTransferServiceImpl.class);

    @Override
    @Transactional
    public Map<String, Object> cashTransfer(CashTransferSupplier supplier, EntityOwner owner, String fBank, CashReceiver cashReceiver) throws SupplierApiUpgradeException, BadAccessException, TransferFailureException, IOException {
        if (owner == null) {
            //获取默认主体
        }
        //获取付款账户的密钥和信息,
        OwnerAccount ownerAccount = owner.getOwnerAccount(fBank);
        Map<String, Object> result = new HashMap<>();
        //调用供应商的转账服务
        LocalDateTime arriveTime = null;
        arriveTime = supplier.cashTransfer(ownerAccount, cashReceiver);
//        try {
//            arriveTime = supplier.cashTransfer(ownerAccount, cashReceiver);
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.error("网络存在异常"+ e.toString());
//        } catch (SupplierApiUpgradeException e) {
//            e.printStackTrace();
//            log.error(e.toString()+"经销商的api需要更新了!");
//        } catch (BadAccessException e) {
//            e.printStackTrace();
//            log.error(e.toString()+"密钥错误");
//        } catch (TransferFailureException e) {
//            e.printStackTrace();
//            resultMessage.append(e.getMessage());
//        }


        return result;
    }
}
