package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.manage.ManageServiceTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class ManageControllerTest extends ManageServiceTest {

    @Override
    protected Login allRunWith() {
        return newRandomManager();
    }

    @Test
    public void go() {
        driver.get("http://localhost/manage");
        assertThat(driver.getTitle())
                .contains("公司后台管理");
    }
}