package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.Salesman;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author CJ
 */
public class SalesmanServiceTest extends CoreServiceTest {

    @Autowired
    private SalesmanService salesmanService;

    @Test
    public void go() {
        Login login = newRandomLogin();
        Salesman salesman = salesmanService.newSalesman(login, null, null);
        System.out.println(salesman);
        assertThat(salesmanService.get(login.getId()))
                .as("保存的销售人员可以成功")
                .isNotNull();
        // 销售人员可以通过指令查询最近的销售记录
        // 今天9酒店 得查看下公众号互动

        assertThat(salesmanService.all(salesman))
                .as("刚开始是没有业绩的")
                .isEmpty();
        final Login targetLogin = newRandomLogin();
        salesmanService.salesmanShareTo(salesman.getId(), targetLogin);
        assertThat(salesmanService.all(salesman))
                .as("邀请了才有业绩")
                .hasSize(1);

        // 被订单系统获取只有
        assertThat(salesmanService.pick(targetLogin))
                .as("肯定可以拾取业绩")
                .isNotNull();
        // 此时业绩是被pick的
        assertThat(salesmanService.all(salesman).get(0).isPicked())
                .as("已经被订单系统获取")
                .isTrue();
    }

}