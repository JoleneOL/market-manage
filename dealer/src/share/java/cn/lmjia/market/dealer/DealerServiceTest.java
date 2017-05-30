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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;
import java.util.function.Function;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
     * @return 已保存的代理商的登录
     */
    protected Login newRandomAgent() {
        return newRandomAgent(UUID.randomUUID().toString());
    }

    /**
     * 新增并且保存一个随机的顶级代理商
     *
     * @return 已保存的代理商的登录
     */
    protected Login newRandomAgent(AgentLevel superior) {
        return newRandomAgent(superior, (Login) null);
    }

    /**
     * 新增并且保存一个随机的特定代理商
     *
     * @param superior  可选定恩上级代理商
     * @param recommend 推荐者
     * @return 已保存的代理商的登录
     */
    protected Login newRandomAgent(AgentLevel superior, Login recommend) {
        return newRandomAgent(UUID.randomUUID().toString(), superior, recommend);
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
        return newRandomAgent(rawPassword, superior, null);
    }

    /**
     * 新增并且保存一个随机的代理商
     *
     * @param rawPassword 明文密码
     * @param superior    作为上级;如果是null则作为顶级代理
     * @param recommend   推荐者
     * @return 已保存的代理商的登录
     */
    protected Login newRandomAgent(String rawPassword, AgentLevel superior, Login recommend) {
        Login login = loginService.newLogin(randomMobile(), recommend, rawPassword);
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

    /**
     * 获取代理上数据测试
     * /agentData/list
     *
     * @param requestCustomizer 可选的定制者
     * @return actions
     */
    protected ResultActions agentDataList(Function<MockHttpServletRequestBuilder, MockHttpServletRequestBuilder> requestCustomizer) throws Exception {
        final String targetListUri = "/agentData/list";

        MockHttpServletRequestBuilder requestBuilder = get(targetListUri);
        if (requestCustomizer != null)
            requestBuilder = requestCustomizer.apply(requestBuilder);
        return mockMvc.perform(
                requestBuilder
        )
                .andExpect(similarJQueryDataTable("classpath:/dealer-view/mock/agentData.json"));
    }

    /**
     * 获取订单数据测试，并且会测试数据结构
     *
     * @param requestCustomizer 可选的定制者
     * @return actions
     */
    protected ResultActions orderDataList(Function<MockHttpServletRequestBuilder
            , MockHttpServletRequestBuilder> requestCustomizer) throws Exception {
        return orderDataList(requestCustomizer, true);
    }

    /**
     * 获取订单数据测试
     *
     * @param requestCustomizer 可选的定制者
     * @param withResultCheck   是否检查数据结构
     * @return actions
     */
    protected ResultActions orderDataList(Function<MockHttpServletRequestBuilder
            , MockHttpServletRequestBuilder> requestCustomizer, boolean withResultCheck) throws Exception {
        MockHttpServletRequestBuilder builder = get("/orderData/manageableList");
        if (requestCustomizer != null)
            builder = requestCustomizer.apply(builder);
        ResultActions actions = mockMvc.perform(
                builder
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        if (withResultCheck)
            return actions.andExpect(similarJQueryDataTable("classpath:/dealer-view/mock/orderData.json"));
        return actions;
    }

    /**
     * 形成一个具备每个级别都有一个独立代理商的代理系统
     *
     * @param login
     * @param levels
     */
    protected void initAgentSystem(Login[] login, AgentLevel[] levels) {
        for (int i = 0; i < login.length; i++) {
            if (i == 0) {
                login[i] = newRandomAgent();
            } else {
                login[i] = newRandomAgent(levels[i - 1]);
            }
            levels[i] = agentService.getAgent(login[i], i);
        }
    }
}
