package cn.lmjia.market.wechat.controller.withdraw;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.WithdrawService;
import cn.lmjia.market.manage.page.ManageWithdrawPage;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.WechatMyPage;
import cn.lmjia.market.wechat.page.WechatWithdrawPage;
import cn.lmjia.market.wechat.page.WechatWithdrawRecordPage;
import cn.lmjia.market.wechat.page.WechatWithdrawVerifyPage;
import com.huotu.verification.repository.VerificationCodeRepository;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;


public class WechatWithdrawControllerTest extends WechatTestBase {

    private static final Log log = LogFactory.getLog(WechatWithdrawControllerTest.class);
    @Autowired
    private ReadService readService;
    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private SystemStringService systemStringService;

    @Before
    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
        // 基于 https://github.com/JoleneOL/market-manage/issues/176 的调整这里先将最低额度调整至1
        systemStringService.updateSystemString(WechatWithdrawController.MARKET_WITHDRAW_MIN_AMOUNT, 1);
    }

    @Test
    @Ignore
    public void goWithdrawAccessDenied() throws InterruptedException {
        //新用户
        Login login = newRandomLogin();
        updateAllRunWith(login);
        //断言没有体现余额
        WechatMyPage myPage = getWechatMyPage();
        myPage.assertWithdrawAble()
                .as("这个时候没有可提现余额")
                .isCloseTo(BigDecimal.ZERO, Offset.offset(new BigDecimal("0.000000000001")));
        // 成功下了一笔订单 获得佣金X
        makeSuccessOrder(login);
        //可提现金额
        BigDecimal amount = readService.currentBalance(login).getAmount();
        myPage = getWechatMyPage();
        myPage.assertWithdrawAble()
                .as("有了")
                .isCloseTo(amount, Offset.offset(new BigDecimal("0.000000000001")))
                .isGreaterThan(BigDecimal.ZERO);
        WechatWithdrawPage withdrawPage = myPage.toWithdrawPage();
        BigDecimal toWithdraw = amount.subtract(BigDecimal.ONE).divideToIntegralValue(new BigDecimal("2"));

        log.info("toWithdraw" + toWithdraw);
        withdrawPage.randomRequestWithoutInvoice(toWithdraw.toString());

        WechatWithdrawVerifyPage verifyPage = initPage(WechatWithdrawVerifyPage.class);
        Thread.sleep(1000);
        // 此时验证手机号码
        verifyPage.submitCode("1234");
        // 成功验证
        // 会看到可提现金额为X-Y

        myPage = getWechatMyPage();
        myPage.assertWithdrawAble()
                .as("看到已经扣除正在提现的金额")
                .isCloseTo(amount.subtract(toWithdraw), Offset.offset(new BigDecimal("0.000000000001")));
        managerReject(login);

        /*//新提现申请
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setWhose(login);
        withdrawRequest.setRequestTime(LocalDateTime.now());
        withdrawRequest.setWithdrawStatus(WithdrawStatus.success);

        withdrawRequestRepository.save(withdrawRequest);
        withdrawService.withdrawNew(login,
                "小王",
                "6215199004049999888",
                "中国银行",
                "13988776655",
                new BigDecimal(123),
                "111",
                "天天快递");
*/
        //managerApproval(login);
        updateAllRunWith(login);
//        LocalDate localDate = LocalDate.now();
//        if (TimeUtil.beforeTheDate(localDate, 6) || TimeUtil.timeFrame(localDate, 15, 21)) {
//            List<WithdrawRequest> resultList = withdrawService.descTimeAndSuccess(login);
//            if (resultList.size() != 0) {
//                //获取最新成功提现记录日期.
//                LocalDateTime lastDateTime = resultList.get(0).getRequestTime();
//                LocalDate lastTime = lastDateTime.toLocalDate();
//                //成功提现记录日期是否是当月1-5日.
//                if (TimeUtil.beforeTheDate(lastTime, 6)) {
//                    //当前日期是否不是16-20日,如果不是说明是1-5日之间第二次提现.跳转提示页面.
//                    if (!TimeUtil.timeFrame(localDate, 15, 21)) {
//                        System.out.println("1-5日重复申请");
//                    }
//                } else {
//                    System.out.println("错误日期申请");
//                }
//            } else {
//                System.out.println("可以提现申请");
//            }
//        }else{
//            System.out.println("错误的申请日期.");
//        }
    }

    @Test
    public void go() throws InterruptedException {
        // 测试就是校验我们的工作成功
        // 就提现这个功能而言 我们要做的测试很简单
        // 1，新用户
        // 尝试提现 会看到可以可提现金额为0
        // 强行输入提现 比如 1元 会看到错误信息
        //
        //设置一个财务
        //Manager manager = new Manager();
        Manager manager = newRandomManager(ManageLevel.finance);
        //绑定微信号
        bindDeveloperWechat(manager);

        Login login = newRandomLogin();
        updateAllRunWith(login);

        WechatMyPage myPage = getWechatMyPage();
        myPage.assertWithdrawAble()
                .as("这个时候没有可提现余额")
                .isCloseTo(BigDecimal.ZERO, Offset.offset(new BigDecimal("0.000000000001")));


        // JS前端依然限制了
//        WechatWithdrawPage withdrawPage = myPage.toWithdrawPage();
//        log.info("准备失败的提现");
//        withdrawPage.randomRequestWithoutInvoice("1");
//        withdrawPage.reloadPageInfo();
//        withdrawPage.assertHaveTooltip();

        // 2，新用户
        // 成功下了一笔订单 获得佣金X
        makeSuccessOrder(login);
        // 尝试提现 会看到可提现金额为X
        BigDecimal amount = readService.currentBalance(login).getAmount();
        myPage = getWechatMyPage();
        myPage.assertWithdrawAble()
                .as("有了")
                .isCloseTo(amount, Offset.offset(new BigDecimal("0.000000000001")));
        // 强行输入 X+1 会看到错误信息
//        log.info("准备失败的提现");
        WechatWithdrawPage withdrawPage = myPage.toWithdrawPage();
//        withdrawPage.randomRequestWithoutInvoice(amount.add(BigDecimal.ONE).toString());
//        withdrawPage.reloadPageInfo();
//        withdrawPage.assertHaveTooltip();
        // 调整为输入Y (Y<X)
        BigDecimal toWithdraw = amount.subtract(BigDecimal.ONE).divideToIntegralValue(new BigDecimal("2"));

        log.info("toWithdraw" + toWithdraw);
        withdrawPage.randomRequestWithoutInvoice(toWithdraw.toString());
//        withdrawPage.printThisPage();

        WechatWithdrawVerifyPage verifyPage = initPage(WechatWithdrawVerifyPage.class);
        Thread.sleep(1000);
        // 此时验证手机号码
        verifyPage.submitCode("1234");
        // 成功验证
        // 会看到可提现金额为X-Y

        myPage = getWechatMyPage();
        myPage.assertWithdrawAble()
                .as("看到已经扣除正在提现的金额")
                .isCloseTo(amount.subtract(toWithdraw), Offset.offset(new BigDecimal("0.000000000001")));
        // Px---以管理员身份
        // 此时会看到该提现申请
        // 点击拒绝
        managerReject(login);
        // -- 回到用户身份
        updateAllRunWith(login);
        // 会看到可提现金额回到X
        myPage = getWechatMyPage();
        myPage.assertWithdrawAble()
                .as("恢复原金额")
                .isCloseTo(amount, Offset.offset(new BigDecimal("0.000000000001")));
        //
        // 重复流程2直置Px
        // 在重复之前需要先删除该手机号码的提现验证码记录
        deleteVC();
        withdrawPage = myPage.toWithdrawPage();
        withdrawPage.randomRequestWithoutInvoice(toWithdraw.toString());
        verifyPage = initPage(WechatWithdrawVerifyPage.class);
        // 此时验证手机号码
        Thread.sleep(1000);
        verifyPage.submitCode("1234");

        // 此时会看到该提现申请
        // 点击同意
        managerApproval(login);
        // -- 回到用户身份
        // 会看到可提现金额回到X-Y
        updateAllRunWith(login);
        myPage = getWechatMyPage();
        myPage.assertWithdrawAble()
                .as("看到已经扣除正在提现的金额")
                .isCloseTo(amount.subtract(toWithdraw), Offset.offset(new BigDecimal("0.000000000001")));
        // 同时看到已提现金额为Y

        // 使用发票提现
        // 在重复之前需要先删除该手机号码的提现验证码记录
        deleteVC();
        withdrawPage = myPage.toWithdrawPage();
        withdrawPage.randomRequestWithInvoice(toWithdraw.toString());
        verifyPage = initPage(WechatWithdrawVerifyPage.class);
        // 此时验证手机号码
        Thread.sleep(1000);
        verifyPage.submitCode("1234");

        // 回来看看提现记录呗
        myPage = getWechatMyPage();
        WechatWithdrawRecordPage recordPage = myPage.toWithdrawRecordPage();
        if (System.getProperty("os.name").contains("Mac")) {
            Thread.sleep(1000);
            recordPage.printThisPage();
        }
        // 管理员可以看到一个发票的申请
//        managerSawInvoice(login);
    }

    private void deleteVC() {
        verificationCodeRepository.findAll().stream()
                .filter(verificationCode -> verificationCode.getType() == withdrawService.withdrawVerificationType().id())
                .forEach(verificationCode -> verificationCodeRepository.delete(verificationCode));
    }

    /**
     * 可以看到这个login提交的有发票的申请
     *
     * @param login
     */
    private void managerSawInvoice(Login login) {
        updateAllRunWith(newRandomManager(ManageLevel.root));
        ManageWithdrawPage.of(this, driver)
                .assertInvoice(readService.nameForPrincipal(login))
                .isTrue();
    }

    /**
     * 同意这个login最近的申请
     *
     * @param login
     */
    private void managerApproval(Login login) throws InterruptedException {
        updateAllRunWith(newRandomManager(ManageLevel.root));
        ManageWithdrawPage.of(this, driver)
                .approval(readService.nameForPrincipal(login));
    }

    /**
     * 拒绝这个login最近的申请
     *
     * @param login
     */
    private void managerReject(Login login) throws InterruptedException {
        updateAllRunWith(newRandomManager(ManageLevel.root));
        ManageWithdrawPage.of(this, driver)
                .reject(readService.nameForPrincipal(login));
    }

}
