package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest;
import cn.lmjia.market.core.repository.WithdrawRequestRepository;
import cn.lmjia.market.core.service.WithdrawService;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.service.VerificationCodeService;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WithdrawServiceImpl implements WithdrawService {

    @Autowired
    private WithdrawRequestRepository withdrawRequestRepository;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private SystemStringService systemStringService;

    @Override
    public WithdrawRequest withdrawNew(Login who, String payee, String account, String bank, String mobile, BigDecimal amount
            , String logisticsCode, String logisticsCompany) {

        WithdrawRequest request = new WithdrawRequest();
        request.setWhose(who);
        request.setRequestTime(LocalDateTime.now());
        request.setAccount(account);
        request.setBank(bank);
        request.setMobile(mobile);
        request.setAmount(amount);
        request.setWithdrawStatus(WithdrawStatus.checkPending);

        if (logisticsCode == null) {
            request.setInvoice(false);
            BigDecimal cost = getCostRateForNoInvoice();
            request.setActualAmount(amount.multiply(BigDecimal.ONE.subtract(cost)));
        } else {
            request.setInvoice(true);
            request.setActualAmount(amount);
            request.setLogisticsCode(logisticsCode);
            request.setLogisticsCompany(logisticsCompany);
        }

        return withdrawRequestRepository.save(request);
    }

    @Override
    public BigDecimal getCostRateForNoInvoice() {
        return systemStringService.getCustomSystemString("withdraw.noInvoice.cost", null
                , true, BigDecimal.class, new BigDecimal("0.2"));
    }

    @Override
    public void checkWithdrawCode(String mobile, String code) throws IllegalVerificationCodeException {
        verificationCodeService.verify(mobile, code, withdrawVerificationType());
    }
}
