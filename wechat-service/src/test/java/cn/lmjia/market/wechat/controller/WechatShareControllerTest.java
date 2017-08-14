package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.wechat.WechatTestBase;
import org.junit.Test;

/**
 * @author CJ
 */
public class WechatShareControllerTest extends WechatTestBase {

    @Test
    public void share() throws Exception {
        // 正常用户进入
        final Login newUser = createNewUserByShare();

        System.out.println(newUser);
    }

}