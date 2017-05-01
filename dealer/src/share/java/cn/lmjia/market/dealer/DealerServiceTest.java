package cn.lmjia.market.dealer;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.dealer.config.DealerConfig;
import cn.lmjia.market.dealer.service.AgentService;
import cn.lnjia.market.core.CoreServiceTest;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = DealerConfig.class)
public abstract class DealerServiceTest extends CoreServiceTest {

    @Autowired
    private AgentService agentService;

    /**
     * 新增并且保存一个随机的顶级代理商
     *
     * @param rawPassword 明文密码
     * @return 已保存的代理商的登录
     */
    protected Login newRandomAgent(String rawPassword) {
        return newRandomAgent(rawPassword, null);
    }

    /**
     * 新增并且保存一个随机的代理商
     *
     * @param rawPassword 明文密码
     * @param superior    作为上级;如果是null则作为顶级代理
     * @return 已保存的代理商的登录
     */
    protected Login newRandomAgent(String rawPassword, AgentLevel superior) {
        Login login = new Login();
        login.setLoginName(randomMobile());
        login = loginService.password(login, rawPassword);
        agentService.addAgent(login, "随机代理" + RandomStringUtils.randomAlphabetic(4), superior);
        return login;
    }

}
