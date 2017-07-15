package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.manage.ManageServiceTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 做一下页面测试
 *
 * @author CJ
 */
public class ManagePromotionRequestControllerTest extends ManageServiceTest {

    private Login current;

    @Override
    protected Login allRunWith() {
        return current;
    }

    @Test
    public void go() {
        current = newRandomManager(ManageLevel.agentManager);

        driver.get("http://localhost/managePromotionRequest");
        assertThat(driver.getTitle())
                .isEqualTo("升级合伙人申请");

    }
}