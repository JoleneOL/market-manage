package cn.lmjia.market.wechat.controller.my;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.model.MemberInfo;
import cn.lmjia.market.wechat.page.WechatMyPage;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 我的可以分为，我的团队，我的佣金
 * 重点测试我的团队
 *
 * @author CJ
 */
public class WechatMyControllerTest extends WechatTestBase {

    private static final Log log = LogFactory.getLog(WechatMyControllerTest.class);
    private Login login;
    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private ReadService readService;

    @Override
    protected Login allRunWith() {
        return login;
    }

    @Test
    public void myTeam() throws InterruptedException {
        // 假定我足够的高级 比如是一个顶级代理商 那么他可以看到下一季的
        Login master = newRandomAgent();

        // 次级代理
        randomAgentTree(master);
        // 找旗下一个代理然后开干
        login = getSubAgents(master).stream()
                .max(new RandomComparator()).orElse(null).getLogin();

        WechatMyPage myPage = getWechatMyPage();

        // 已经成为代理商的人
        Set<Login> agentLogin = agentLevelRepository.findAll().stream()
                .map(AgentLevel::getLogin)
                .collect(Collectors.toSet());
//        Login currentLogin = login;
        AgentLevel currentAgent = agentService.highestAgent(login);
        while (true) {
            final List<AgentLevel> subAgents = getSubAgents(currentAgent);
            if (subAgents.isEmpty()) {
                // 可以看看到 currentLogin 招募的爱心天使
                log.info("最后一个等级，应该可以看到爱心天使了");
                // 而且应该排除掉那些已经成为代理商的人
                myPage.assertTeamMembers(
                        loginRepository.findByGuideUserAndSuccessOrderTrue(currentAgent.getLogin())
                                .stream()
                                .filter(login1 -> !agentLogin.contains(login1))
                                .map(this::fromLogin)
                                .collect(Collectors.toList())
                );
                break;
            } else {
                // 可以看到我旗下的代理
                myPage.assertTeamMembers(subAgents.stream().map(this::fromAgentLevel).collect(Collectors.toList()));
                log.info("检测通过，点击下一个");
                AgentLevel nextAgent = subAgents.stream().max(new RandomComparator()).orElse(null);
                myPage.clickMember(fromAgentLevel(nextAgent));
                currentAgent = nextAgent;
            }
        }

    }

    private MemberInfo fromLogin(Login login) {
        MemberInfo info = new MemberInfo();
        info.setName(readService.nameForPrincipal(login));
        info.setJoinDate(login.getCreatedTime().toLocalDate());
        info.setLevel(login.isSuccessOrder() ? "爱心天使" : "普通用户");
        info.setMobile(readService.mobileFor(login));
        return info;
    }

    private MemberInfo fromAgentLevel(AgentLevel agent) {
        MemberInfo info = new MemberInfo();
        info.setName(readService.nameForPrincipal(agent.getLogin()));
        info.setJoinDate(agent.getCreatedTime().toLocalDate());
        info.setLevel(readService.getLoginTitle(agent.getLevel()));
        info.setMobile(readService.mobileFor(agent.getLogin()));
        return info;
    }

    /**
     * @return login旗下的代理
     */
    private List<AgentLevel> getSubAgents(AgentLevel agentLevel) {
        if (agentLevel == null)
            return Collections.emptyList();
        return agentLevelRepository.findBySuperior(agentLevel);
    }

    /**
     * @return login旗下的代理
     */
    private List<AgentLevel> getSubAgents(Login login) {
        return getSubAgents(agentService.highestAgent(login));
    }

    /**
     * 建立一个比较随意的代理树，每一个代理都招募了几个爱心天使
     *
     * @param login
     */
    private void randomAgentTree(Login login) {
        int count = 1 + random.nextInt(2);

        while (count-- > 0) {
            randomAgentTreeReal(login);
        }
    }

    private void randomAgentTreeReal(Login login) {
        AgentLevel topLevel = agentService.highestAgent(login);
        Login subLogin;
        if (topLevel != null) {
            try {
                Login agentLogin = newRandomAgent(topLevel);
                randomAgentTree(agentLogin);
                subLogin = agentLogin;
            } catch (IllegalStateException ex) {
                // 不让加？那就发展一个吧
                subLogin = login;
            }
        } else {
            subLogin = login;
        }
        // 给subLogin 添加1个爱心天使或者普通用户
        // 至少一个爱心天使
        while (true) {
            Login newLogin = loginService.newLogin(Login.class, "新发展的" + RandomStringUtils.randomAlphabetic(10)
                    , subLogin, UUID.randomUUID().toString());
            if (random.nextBoolean()) {
                makeSuccessOrder(newLogin);
                break;
            }
        }
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