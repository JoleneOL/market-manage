package cn.lmjia.market.wechat.controller.my;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.WechatMyPage;
import org.junit.Test;

/**
 * 我的可以分为，我的团队，我的佣金
 * 重点测试我的团队
 * @author CJ
 */
public class WechatMyControllerTest extends WechatTestBase {

    private Login login;

    @Override
    protected Login allRunWith() {
        return login;
    }

    @Test
    public void myTeam() {
        // 假定我足够的高级 比如是一个顶级代理商 那么他可以看到下一季的
        login = loginService.byLoginName("master");

        WechatMyPage myPage = getWechatMyPage();

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