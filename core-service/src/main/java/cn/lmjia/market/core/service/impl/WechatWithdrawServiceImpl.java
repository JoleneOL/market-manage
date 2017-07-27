package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.Invoice;
import cn.lmjia.market.core.entity.withdraw.Withdraw;
import cn.lmjia.market.core.repository.WechatWithdrawRepository;
import cn.lmjia.market.core.service.WechatWithdrawService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WechatWithdrawServiceImpl implements WechatWithdrawService{

    private static final Log log = LogFactory.getLog(WechatWithdrawServiceImpl.class);

    @Autowired
    private WechatWithdrawRepository wechatWithdrawRepository;

    @Override
    public Withdraw withdrawNew(String payee, String account, String bank, String mobile, BigDecimal withdrawMoney, String logisticsNumber,String logisticsCompany ) {

        Invoice invoice = new Invoice();
        invoice.setCompanyName("利每家科技有限公司");
        invoice.setTaxnumber("91330108MA28MBU173");
        invoice.setLogisticsnumber(logisticsNumber);
        invoice.setLogisticscompany(logisticsCompany);

        Withdraw withdraw = new Withdraw();
        withdraw.setAccount(account);
        withdraw.setBank(bank);
        withdraw.setMobile(mobile);
        withdraw.setWithdrawMoney(withdrawMoney);
        withdraw.setInvoice(invoice);
        withdraw.setWithdrawStatus(WithdrawStatus.checkPending);
        return wechatWithdrawRepository.save(withdraw);
    }
}
