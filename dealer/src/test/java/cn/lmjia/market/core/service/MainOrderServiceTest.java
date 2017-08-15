package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.dealer.DealerServiceTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MainOrderServiceTest extends DealerServiceTest {

    @Test
    public void newOrder() throws Exception {
        Login login1 = randomLogin(false);
        Login login2 = randomLogin(false);
        newRandomOrderFor(login1, login2);
//        newRandomOrderFor(login1, login2);
//        newRandomOrderFor(login1, login2);
        assertThat(true)
                .isTrue();
    }

}