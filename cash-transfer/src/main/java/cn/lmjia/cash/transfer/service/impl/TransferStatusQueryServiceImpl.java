package cn.lmjia.cash.transfer.service.impl;

import cn.lmjia.cash.transfer.CashReceiver;
import cn.lmjia.cash.transfer.CashTransferSupplier;
import cn.lmjia.cash.transfer.EntityOwner;
import cn.lmjia.cash.transfer.OwnerAccount;
import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;
import cn.lmjia.cash.transfer.service.TransferStatusQueryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class TransferStatusQueryServiceImpl implements TransferStatusQueryService {

    @Override
    @Transactional
    public Map<String, Object> statusQuery(EntityOwner owner, CashTransferSupplier supplier, String fBank, CashReceiver receuver) throws TransferFailureException, BadAccessException, JsonProcessingException {
        //获取付款账户的密钥和信息,
        OwnerAccount ownerAccount = owner.getOwnerAccount(fBank);
        return supplier.statusQuery(ownerAccount,receuver);
    }

}
