package cn.lmjia.market.core.service.impl;

import cn.lmjia.cash.transfer.model.CashTransferResult;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest_;
import cn.lmjia.market.core.repository.WithdrawRequestRepository;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.WithdrawService;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.service.VerificationCodeService;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    @Autowired
    private ReadService readService;

    @Override
    public WithdrawRequest withdrawNew(Login who, String payee, String account, String bank, String mobile, BigDecimal amount
            , String logisticsCode, String logisticsCompany) {

        WithdrawRequest request = new WithdrawRequest();
        request.setPayee(payee);
        request.setWhose(who);
        request.setRequestTime(LocalDateTime.now());
        request.setAccount(account);
        request.setBank(bank);
        request.setMobile(mobile);
        request.setAmount(amount);
        request.setWithdrawStatus(WithdrawStatus.init);

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
    public void submitRequest(Login login, String code) throws IllegalVerificationCodeException {
        verificationCodeService.verify(readService.mobileFor(login), code, withdrawVerificationType());
        withdrawRequestRepository.findAll((root, query, cb) -> cb.and(
                cb.equal(root.get(WithdrawRequest_.whose), login)
                , cb.equal(root.get(WithdrawRequest_.withdrawStatus), WithdrawStatus.init)
        ), new PageRequest(0, 1, Sort.Direction.DESC, "requestTime"))
                .getContent().get(0).setWithdrawStatus(WithdrawStatus.checkPending);
    }

    @Override
    public WithdrawRequest get(long id) {
        return withdrawRequestRepository.getOne(id);
    }

    @Override
    public void reject(Manager manager, long requestId, String comment) {
        WithdrawRequest request = get(requestId);
        request.setWithdrawStatus(WithdrawStatus.refuse);
        request.setManageBy(manager);
        request.setManageTime(LocalDateTime.now());
        request.setComment(comment);
    }

    @Override
    public void approval(Manager manager, long requestId, CashTransferResult result) {

        WithdrawRequest request = get(requestId);
        //银行处理时间
        request.setBankProcessingTime(result.getProcessingTime());
        request.setWithdrawStatus(WithdrawStatus.success);
        request.setManageBy(manager);
        request.setManageTime(LocalDateTime.now());
    }

    @Override
    public void automaticIsSuccessful(long withdrawRequestId, LocalDateTime processingTime) {
        WithdrawRequest request = get(withdrawRequestId);
        request.setBankProcessingTime(processingTime);
        // TODO : 这里还有点疑问是否应该直接变成成功状态
        request.setWithdrawStatus(WithdrawStatus.success);
    }

}
