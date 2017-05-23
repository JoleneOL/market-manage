package cn.lmjia.market.wechat.controller.my;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.wechat.WechatTestBase;
import org.junit.Test;

/**
 * @author CJ
 */
public class WechatMyControllerTest extends WechatTestBase {

    @Override
    protected Login allRunWith() {
        return randomLogin(false);
    }

    @Test
    public void go() {
        getWechatMyPage();
    }
}