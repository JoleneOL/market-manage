package cn.lmjia.cash.transfer.service.impl;

import cn.lmjia.cash.transfer.*;
import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.SupplierApiUpgradeException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;
import cn.lmjia.cash.transfer.service.CashTransferService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
public class CashTransferServiceImpl implements CashTransferService {

    private static final Log log = LogFactory.getLog(CashTransferServiceImpl.class);
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    @Transactional
    public Map<String, Object> cashTransfer(CashTransferSupplier supplier, EntityOwner owner, CashReceiver cashReceiver) throws SupplierApiUpgradeException, BadAccessException, TransferFailureException, IOException {
        if (owner == null) {
            //获取默认主体
            owner = applicationContext.getBean(EntityOwner.class);
        }
        if(supplier == null){
            //默认的经销商
            supplier = applicationContext.getBean(CashTransferSupplier.class);
        }
        //获取付款账户的密钥和信息,
        OwnerAccount ownerAccount = owner.getOwnerAccount(supplier.getName());
        //调用供应商的转账服务
        return supplier.cashTransfer(ownerAccount, cashReceiver);
    }
}
