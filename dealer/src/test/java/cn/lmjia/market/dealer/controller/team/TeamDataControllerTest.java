package cn.lmjia.market.dealer.controller.team;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.cache.LoginRelation;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.repository.cache.LoginRelationRepository;
import cn.lmjia.market.core.service.QuickTradeService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.service.cache.LoginRelationCacheService;
import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.dealer.service.TeamService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author CJ
 */
//@ActiveProfiles({"mysql"})
@ContextConfiguration(classes = SecurityConfig.class)
public class TeamDataControllerTest extends DealerServiceTest {

    @Autowired
    private SystemService systemService;
    @Autowired
    private LoginRelationRepository loginRelationRepository;
    @Autowired
    private LoginRelationCacheService loginRelationCacheService;
    private Login userLogin;
    @Autowired
    private TeamService teamService;
    @Autowired
    private QuickTradeService quickTradeService;

    @Override
    protected Login allRunWith() {
        return userLogin;
    }

    @Test
    public void data() throws Exception {
        userLogin = newRandomAgent();
        //
        assertThat(teamService.all(userLogin))
                .as("一开始的时候自然是0")
                .isEqualTo(0);
        teamListRequestBuilder(null, 0);

        Login als[] = new Login[systemService.systemLevel()];
        AgentLevel as[] = new AgentLevel[systemService.systemLevel()];
        initAgentSystem(als, as);

        assertThat(teamService.all(userLogin))
                .as("一开始的时候自然是0")
                .isEqualTo(0);
        teamListRequestBuilder(null, 0);
        // 引导增加了一个代理商
        newRandomAgent(as[random.nextInt(systemService.systemLevel() - 1)], userLogin);
        assertThat(teamService.all(userLogin))
                .as("多了一个代理商")
                .isEqualTo(1);
        teamListRequestBuilder(null, 1);
        // 新增一个订单
        MainOrder order1 = newRandomOrderFor(als[random.nextInt(systemService.systemLevel())], userLogin);
        assertThat(teamService.all(userLogin))
                .as("又多了一个客户")
                .isEqualTo(2);
        teamListRequestBuilder(null, 2);
        // 但客户只有一个
        assertThat(teamService.customers(userLogin))
                .as("客户还是只有一个的")
                .isEqualTo(1);
        assertThat(teamService.validCustomers(userLogin))
                .as("但是有效客户还是没有")
                .isEqualTo(0);
        teamListRequestBuilder(builder -> builder.param("rank", "4"), 0);

        makeOrderPay(order1);
        quickTradeService.makeDone(order1);
        assertThat(teamService.validCustomers(userLogin))
                .as("现在有了")
                .isEqualTo(1);
        assertThat(teamService.customers(userLogin))
                .as("客户还是只有一个的")
                .isEqualTo(1);
        teamListRequestBuilder(builder -> builder.param("rank", "4"), 1);

        // 就算这个客户再下一单 同样也是1
        MainOrder order2 = newRandomOrderFor(als[random.nextInt(systemService.systemLevel())], userLogin, order1.getCustomer().getMobile());
        makeOrderPay(order2);
        quickTradeService.makeDone(order2);

        assertThat(teamService.validCustomers(userLogin))
                .as("现在有了")
                .isEqualTo(1);
        assertThat(teamService.customers(userLogin))
                .as("客户还是只有一个的")
                .isEqualTo(1);
        teamListRequestBuilder(builder -> builder.param("rank", "4"), 1);
    }

    @Test
    public void dataFor2() throws Exception {
        Login als[] = new Login[systemService.systemLevel()];
        AgentLevel as[] = new AgentLevel[systemService.systemLevel()];
        initAgentSystem(als, as);
        // 然后每一个人 都增加一个客户
        for (Login login : als) {
            newRandomOrderFor(login, randomLogin(false));
        }

//        loginRelationCacheService.rebuildAgentSystem(as[0].getSystem());

        loginRelationRepository.findBySystem(as[0].getSystem())
                .stream()
                .sorted(new Comparator<LoginRelation>() {
                    @Override
                    public int compare(LoginRelation o1, LoginRelation o2) {
                        return (int) ((o1.getFrom().getId() - o2.getFrom().getId()) * 1000000
                                + (o1.getTo().getId() - o2.getTo().getId()) * 1000
                                + o1.getLevel() - o2.getLevel());
                    }
                })
                .forEach(System.out::println);

        for (int i = 0; i < als.length; i++) {
            userLogin = als[i];
            // 如果我身处 i 层
            // 那么我拥有的代理下线有 4-i
            // 每个人都拥有一个直接客户，所有我这里拥有客户数量是 5-i
            int agents = systemService.systemLevel() - 1 - i;
            int customers = systemService.systemLevel() - i;


            int[] numbers = new int[systemService.systemLevel() + 1];
            Arrays.setAll(numbers, (t) -> 0);
            numbers[numbers.length - 1] = customers;
            for (int j = 0; j < systemService.systemLevel(); j++) {
                // j 的数量是多少？
                if (j > i) {
                    // 只有高级才可以拥有低级
                    // i:0 j:1 -> 1
                    // i:0 j:2 -> 2
                    // i:1 j:2
                    numbers[j] = j - i;
                }
            }

            teamList2RequestBuilder(null, agents + customers);
            // // 总 1
            // 分 2
            // 经销 3 我们认定最低级为3的前提下
            // 客户 4
            final int count = 4;
            int x = count;
            while (x-- > 0) {
                int rank = x + 1;
                teamList2RequestBuilder(builder -> builder.param("rank", String.valueOf(rank))
                        , numbers[numbers.length - (count - x)]);
            }
        }

//        for (Login login : als) {
//            userLogin = login;
//            mockMvc.perform(get("/api/teamList"))
//                    .andDo(print());
//        }

    }

    private ResultActions teamList2RequestBuilder(Function<MockHttpServletRequestBuilder
            , MockHttpServletRequestBuilder> b, int expectedSize) throws Exception {
        return teamList2RequestBuilder(b)
                .andExpect(jsonPath("$.data.length()").value(expectedSize))
                .andExpect(jsonPath("$.total_count").value(expectedSize));
    }

    private ResultActions teamList2RequestBuilder(Function<MockHttpServletRequestBuilder
            , MockHttpServletRequestBuilder> b) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/teamList2");
        if (b != null)
            requestBuilder = b.apply(requestBuilder);
        return mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private ResultActions teamListRequestBuilder(Function<MockHttpServletRequestBuilder
            , MockHttpServletRequestBuilder> b, int expectedSize) throws Exception {
        return teamListRequestBuilder(b)
                .andExpect(jsonPath("$.data.length()").value(expectedSize));
    }

    private ResultActions teamListRequestBuilder(Function<MockHttpServletRequestBuilder
            , MockHttpServletRequestBuilder> b) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/teamList");
        if (b != null)
            requestBuilder = b.apply(requestBuilder);
        return mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

}