package cn.lmjia.market.web.controller;

import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.LoginService;
import me.jiangcai.lib.seext.EnumUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CJ
 */
public class WelcomeControllerTest extends WebTest {
    @Autowired
    private LoginService loginService;

    @Test
    public void index() throws Exception {
        // 管理员登录
        String rawPassword = randomEmailAddress();
        Manager manager = newRandomManager(rawPassword, ManageLevel.root);

        mockMvc.perform(get(""))
                .andDo(print());

        // 代理商登录
    }

    /**
     * 新增并且保存一个随机的管理员
     *
     * @param rawPassword 明文密码
     * @param levels      等级;可以为null
     * @return 已保存的管理员
     */
    private Manager newRandomManager(String rawPassword, ManageLevel... levels) {
        Manager manager = new Manager();
        manager.setLoginName(randomMobile());
        manager.setLevel(EnumUtils.randomEnum(ManageLevel.class, levels));
        return loginService.password(manager, rawPassword);
    }

}