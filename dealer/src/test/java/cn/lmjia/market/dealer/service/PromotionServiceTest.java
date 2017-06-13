package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.cache.LoginRelation;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import cn.lmjia.market.core.repository.cache.LoginRelationRepository;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.service.cache.LoginRelationCacheService;
import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 晋升测试
 *
 * @author CJ
 */
public class PromotionServiceTest extends DealerServiceTest {

    @Autowired
    private PromotionService promotionService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private LoginRelationCacheService loginRelationCacheService;
    @Autowired
    private LoginRelationRepository loginRelationRepository;

    @Test
    public void go() {
        boolean debug = true;
        // 首先构造一个代理体系
        Login agentLoginRoot = newRandomAgent();
        AgentLevel agentRoot = agentService.highestAgent(agentLoginRoot);
        // 设置每一个级别的晋级标准 都为1-3

        int[] ups = new int[systemService.systemLevel()];
        for (int i = 0; i < systemService.systemLevel(); i++) {
            assertThat(promotionService.promotionCountForAgentLevel(i))
                    .isEqualTo(5);
            ups[i] = 2 + (debug ? 0 : random.nextInt(3)); //
            promotionService.updatePromotionCountForAgentLevel(i, ups[i]);
        }

//         最早它只是一个客户 :)
        MainOrder firstOrder = newRandomOrderFor(agentLoginRoot, agentLoginRoot);
        makeOrderPay(firstOrder);
        makeOrderDone(firstOrder);

        // ok 现在需要升级了 从等级中倒过来
        Login login = firstOrder.getCustomer().getLogin();

        makeLoginTo(agentLoginRoot, login, ups, systemService.systemLevel() - 2);

        // 第一步 检查所有的代理商逻辑是OK的
        final AgentSystem system = agentRoot.getSystem();
        agentService.healthCheck(system);
        // 这下是深层次了
        // 41
        long current = loginRelationRepository.countBySystem(system);
        List<LoginRelation> currentList = loginRelationRepository.findBySystem(system);
        final Comparator<LoginRelation> c = (o1, o2) -> (int) (o1.getFrom().getId() * 1000000 - o2.getFrom().getId() * 1000000 + o1.getTo().getId() * 1000 - o2.getTo().getId() * 1000 + o1.getLevel() - o2.getLevel());
        currentList.sort(c);
        loginRelationCacheService.rebuildAgentSystem(system);

        List<LoginRelation> allList = loginRelationRepository.findBySystem(system);
        allList.sort(c);
        allList.removeAll(currentList);
        allList.forEach(System.out::println);

        assertThat(loginRelationRepository.countBySystem(system))
                .isEqualTo(current);


    }

    /**
     * 让login成为 level 代理商
     *
     * @param owner 下单者
     * @param login login
     * @param ups   需要数量
     * @param level 目标等级
     */
    private void makeLoginTo(Login owner, Login login, int[] ups, int level) {

        if (level >= ups.length) {
            // 这个时候只需要成交即可
            MainOrder order = newRandomOrderFor(owner, login.getGuideUser(), login.getLoginName());
            makeOrderPay(order);
            makeOrderDone(order);
            return;
        }

        // 如果要升级到level 得先升级到level+1
        makeLoginTo(owner, login, ups, level + 1);
        // 然后邀请到足够的 同级选手
        int count = ups[level] - 1;
        while (count-- > 0) {
            // 发展出来的人
            Login sub = newRandomOrderFor(owner, login).getCustomer().getLogin();
            // 让它发展出来的人 发展到level+1等级
            makeLoginTo(owner, sub, ups, level + 1);
        }
        // 这个时候 它还没到目标等级
        final AgentLevel agentLevel = agentService.highestAgent(login);
        if (agentLevel != null)
            assertThat(agentLevel.getLevel())
                    .as("数量不足，应该还是无法成为" + level)
                    .isGreaterThan(level);
        // 再来一个
        Login sub = newRandomOrderFor(owner, login).getCustomer().getLogin();
        makeLoginTo(owner, sub, ups, level + 1);
        final AgentLevel agentLevel1 = agentService.highestAgent(login);
        assertThat(agentLevel1)
                .as("满足了条件，必然升级成为了代理商")
                .isNotNull();
        assertThat(agentLevel1.getLevel())
                .as("奖励足够，应该成为" + level)
                .isEqualTo(level);
    }

}