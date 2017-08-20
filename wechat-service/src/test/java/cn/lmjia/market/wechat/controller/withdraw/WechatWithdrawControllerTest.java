package cn.lmjia.market.wechat.controller.withdraw;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.wechat.WechatTestBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Ignore
public class WechatWithdrawControllerTest extends WechatTestBase {

    private static final Log log = LogFactory.getLog(WechatWithdrawControllerTest.class);

    @Test
    public void go() {
        // 测试就是校验我们的工作成功
        // 就提现这个功能而言 我们要做的测试很简单
        // 1，新用户
        // 尝试提现 会看到可以可提现金额为0
        // 强行输入提现 比如 1元 会看到错误信息
        //
        // 2，新用户
        // 成功下了一笔订单 获得佣金X
        // 尝试提现 会看到可提现金额为X
        // 强行输入 X+1 会看到错误信息
        // 调整为输入Y (Y<X)
        // 此时验证手机号码
        // 成功验证
        // 会看到可提现金额为X-Y
        // Px---以管理员身份
        // 此时会看到该提现申请
        // 点击拒绝
        // -- 回到用户身份
        // 会看到可提现金额回到X
        //
        // 重复流程2直置Px
        // 此时会看到该提现申请
        // 点击同意
        // -- 回到用户身份
        // 会看到可提现金额回到X-Y
        // 同时看到已提现金额为Y
    }

    @Test
    public void doWithdraw() throws Exception {
        Login user = createNewUserByShare();
        bindDeveloperWechat(user);
        updateAllRunWith(user);

        String withdrawUri = mockMvc.perform(wechatPost("/wechatWithdraw")
                .param("payee", "oneal")
                .param("account", "6217001480003532428")
                .param("bank", "建设银行")
                .param("mobile", "15267286525")
                .param("withdrawMoney", "500.00")
                .param("invoice", "0")
//                .param("logisticsNumber", "710389211847")
//                .param("logisticsCompany", "圆通物流")
        )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getHeader("Location");

        driver.get("http://localhost" + withdrawUri);

    }

    @Test
    public void withdrawVerify() throws Exception {
        Login user = createNewUserByShare();
        bindDeveloperWechat(user);
        updateAllRunWith(user);

        String withdrawUri = mockMvc.perform(wechatPost("/misc/sendWithdrawCode")
                        .param("mobile", "15267286525")
        )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getHeader("Location");

        driver.get("http://localhost" + withdrawUri);

    }


}
