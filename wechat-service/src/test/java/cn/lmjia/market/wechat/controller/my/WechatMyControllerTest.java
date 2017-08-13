package cn.lmjia.market.wechat.controller.my;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.wechat.WechatTestBase;
import org.junit.Test;

/**
 * @author CJ
 */
public class WechatMyControllerTest extends WechatTestBase {

    private Login login;

    @Override
    protected Login allRunWith() {
        return login;
    }

    @Test
    public void go() {
        login = randomLogin(false);
        visitWechat();

        // 弄一个订单
        final String mobile = randomMobile();
        newRandomOrderFor(login, login, mobile);
        login = loginService.byLoginName(mobile);

        visitWechat();
    }

    private void visitWechat() {
        getWechatMyPage();
//        getWechatMyTeamPage();
        getWechatOrderListPage();
    }
}