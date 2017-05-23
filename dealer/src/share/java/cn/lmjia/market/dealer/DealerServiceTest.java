package cn.lmjia.market.dealer;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.service.ContactWayService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.dealer.config.DealerConfig;
import cn.lmjia.market.dealer.page.AgentManageMainPage;
import cn.lmjia.market.dealer.service.AgentService;
import me.jiangcai.lib.seext.function.AllBiConsumer;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {DealerConfig.class})
public abstract class DealerServiceTest extends CoreServiceTest {

    @Autowired
    protected AgentService agentService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ContactWayService contactWayService;

    /**
     * @return 代理商管理入口页面
     */
    protected AgentManageMainPage mainPage() {
        driver.get("http://localhost/agentMain");
        return initPage(AgentManageMainPage.class);
    }

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
        Login login = loginService.newLogin(randomMobile(), null, rawPassword);
        if (random.nextBoolean()) {
            contactWayService.updateMobile(login, randomMobile());
            contactWayService.updateName(login, "新名字" + RandomStringUtils.randomAlphabetic(6));
        }
        agentService.addAgent(null, login, "随机代理" + RandomStringUtils.randomAlphabetic(4), null, null, 0, 0, superior);
        return login;
    }

    /**
     * 添加一个崭新的代理树，并且进行特定检查
     *
     * @param work 特定检查，每个新增的父级代理都会经历一次
     */
    protected void newRandomAgentSystemAnd(AllBiConsumer<Login, AgentLevel> work) throws Exception {
        newRandomAgentSystemAnd(UUID.randomUUID().toString(), work);
    }

    /**
     * 添加一个崭新的代理树，并且进行特定检查
     *
     * @param rawPassword 为此新增身份的明文密码
     * @param work        特定检查，每个新增的父级代理都会经历一次
     */
    protected void newRandomAgentSystemAnd(String rawPassword, AllBiConsumer<Login, AgentLevel> work) throws Exception {
        int i;
        Login rootLogin = newRandomAgent(rawPassword);
        AgentLevel rootAgent = agentService.highestAgent(rootLogin);
        // 建立旗下分支
        i = systemService.systemLevel() - 1;
        AgentLevel newAgent = rootAgent;
        Login newLogin = rootLogin;
        while (i-- > 0) {
            // 当前的代理商的上级是
            AgentLevel currentSuper = newAgent;
            Login currentSuperLogin = newLogin;
            newAgent = null;
            int x = random.nextInt(10) + 1;
            while (x-- > 0) {
                Login login = newRandomAgent(rawPassword, currentSuper);
                if (newAgent == null || random.nextBoolean()) {
                    newAgent = agentService.highestAgent(login);
                    newLogin = login;
                }
            }

            // currentSuper 拥有的代理商
            if (work != null)
                work.accept(currentSuperLogin, currentSuper);
        }
    }
}
