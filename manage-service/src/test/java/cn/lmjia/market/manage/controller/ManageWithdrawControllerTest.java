package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest;
import cn.lmjia.market.core.repository.WithdrawRequestRepository;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.WithdrawService;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageWithdrawPage;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class ManageWithdrawControllerTest extends ManageServiceTest {

    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private ReadService readService;
    @Autowired
    private WithdrawRequestRepository withdrawRequestRepository;

    @Test
    public void go() throws InterruptedException {


        // 可以检查是否存在发票
        Login target = newRandomLogin();
        WithdrawRequest request1 = randomWithdrawRequest(target);

        updateAllRunWith(newRandomManager(ManageLevel.root));
        ManageWithdrawPage page = ManageWithdrawPage.of(this, driver);
        page.reject(readService.nameForPrincipal(target));

        assertThat(withdrawService.get(request1.getWithdrawId()).getWithdrawStatus())
                .isEqualByComparingTo(WithdrawStatus.refuse);

        WithdrawRequest request2 = randomWithdrawRequest(target);

        page.refresh();
        page.approval(readService.nameForPrincipal(target));

        assertThat(withdrawService.get(request2.getWithdrawId()).getWithdrawStatus())
                .isEqualByComparingTo(WithdrawStatus.success);

        // 提交一个带发票的
        randomWithdrawRequestWithInvoice(target);

        page.refresh();
        page.assertInvoice(readService.nameForPrincipal(target))
                .isTrue();

    }

    private void randomWithdrawRequestWithInvoice(Login target) {
        WithdrawRequest request = withdrawService.withdrawNew(target, RandomStringUtils.randomAlphabetic(10)
                , RandomStringUtils.randomNumeric(10), RandomStringUtils.randomAlphabetic(10)
                , randomMobile(), new BigDecimal("100"), RandomStringUtils.randomNumeric(10)
                , RandomStringUtils.randomAlphabetic(10));
        mockPadding(request);
    }

    private void mockPadding(WithdrawRequest request) {
        request.setWithdrawStatus(WithdrawStatus.checkPending);
        withdrawRequestRepository.save(request);
    }

    private WithdrawRequest randomWithdrawRequest(Login target) {
        WithdrawRequest request = withdrawService.withdrawNew(target, RandomStringUtils.randomAlphabetic(10)
                , RandomStringUtils.randomNumeric(10), RandomStringUtils.randomAlphabetic(10)
                , randomMobile(), new BigDecimal("100"), null, null);
        mockPadding(request);
        return request;
    }

}