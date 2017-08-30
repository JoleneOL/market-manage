package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.LoginRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ActiveProfiles("mysql2")
public class LoginServiceTest extends CoreServiceTest {

    @Autowired
    private LoginService loginService;
    @Autowired
    private LoginRepository loginRepository;

    @Test
    public void go() {
        loginService.tryAutoDeleteLogin();
        // 新增一个客户 并且设定它的创建时间为 24分之前 以及 29分钟之前
        // 那么一个将在1分钟后警告 并且在6分钟后删除
        // 另一个将马上警告，并且在1分钟后删除
        Login l1 = loginService.newLogin(Login.class, randomMobile(), loginService.byLoginName("master"), UUID.randomUUID().toString());
        Login l2 = loginService.newLogin(Login.class, randomMobile(), loginService.byLoginName("master"), UUID.randomUUID().toString());

        LocalDateTime now = LocalDateTime.now();
        l1.setCreatedTime(now.minusMinutes(24));
        l2.setCreatedTime(now.minusMinutes(29));

        loginRepository.save(l1);
        loginRepository.save(l2);

        loginService.tryAutoDeleteLogin();

    }

    @Test
    public void time() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime after30M = now.plusMinutes(30);

        assertThat(now.until(after30M, ChronoUnit.MINUTES))
                .isEqualTo(30);
        assertThat(now.until(after30M, ChronoUnit.MILLIS))
                .isEqualTo(30 * 60 * 1000);
    }

}