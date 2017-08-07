package cn.lmjia.market.web;

import cn.lmjia.market.web.controller.WebTest;
import org.junit.Test;

/**
 * @author CJ
 */
public class WorkTest extends WebTest {

    @Test
    public void go() {
        driver.get("http://localhost/");
    }
}
