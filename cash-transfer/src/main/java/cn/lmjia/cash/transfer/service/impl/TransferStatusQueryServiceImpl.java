package cn.lmjia.cash.transfer.service.impl;

import cn.lmjia.cash.transfer.CashReceiver;
import cn.lmjia.cash.transfer.CashTransferSupplier;
import cn.lmjia.cash.transfer.EntityOwner;
import cn.lmjia.cash.transfer.OwnerAccount;
import cn.lmjia.cash.transfer.service.TransferStatusQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class TransferStatusQueryServiceImpl implements TransferStatusQueryService {

    @Override
    @Transactional
    public Map<String, Object> statusQuery(EntityOwner owner, CashTransferSupplier supplier, String fBank, CashReceiver receuver) {
        //获取付款账户的密钥和信息,
        OwnerAccount ownerAccount = owner.getOwnerAccount(fBank);
        Map<String, Object> result = new HashMap<>();
        result = supplier.statusQuery(ownerAccount,receuver);
        return null;
    }

}
