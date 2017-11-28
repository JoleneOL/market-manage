package cn.lmjia.cash.transfer.service.impl;

import cn.lmjia.cash.transfer.CashReceiver;
import cn.lmjia.cash.transfer.CashTransferSupplier;
import cn.lmjia.cash.transfer.EntityOwner;
import cn.lmjia.cash.transfer.OwnerAccount;
import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;
import cn.lmjia.cash.transfer.model.CashTransferResult;
import cn.lmjia.cash.transfer.service.TransferStatusQueryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferStatusQueryServiceImpl implements TransferStatusQueryService {
    private static final Log log = LogFactory.getLog(TransferStatusQueryServiceImpl.class);
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    @Transactional
    public CashTransferResult statusQuery(EntityOwner owner, CashTransferSupplier supplier, CashReceiver receuver) throws TransferFailureException, BadAccessException, JsonProcessingException {
        if (owner == null) {
            owner = applicationContext.getBean(EntityOwner.class);
        }
        if(supplier == null){
            supplier = applicationContext.getBean(CashTransferSupplier.class);
        }
        //获取付款账户的密钥和信息,
        OwnerAccount ownerAccount = owner.getOwnerAccount(supplier.getName());
        return supplier.statusQuery(ownerAccount, receuver);
    }

}
