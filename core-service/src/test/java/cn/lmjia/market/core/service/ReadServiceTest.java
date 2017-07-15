package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class ReadServiceTest extends CoreServiceTest {

    @Autowired
    private ReadService readService;
    @Autowired
    private ContactWayService contactWayService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Test
    public void mobileFor() throws Exception {
        Manager manager = newRandomManager("", ManageLevel.root);
        assertThat(readService.mobileFor(manager))
                .isNotNull();
        // 更新联系方式
        final String mobile = randomMobile();
        contactWayService.updateMobile(manager, mobile);
        assertThat(readService.mobileFor(manager))
                .isEqualTo(mobile);

        assertThat(readService.nameForPrincipal(manager))
                .isNotEmpty();
        final String name = UUID.randomUUID().toString();
        contactWayService.updateName(manager, name);
        assertThat(readService.nameForPrincipal(manager))
                .isEqualTo(name);

        System.out.println(readService.agentLevelForPrincipal(manager));
    }

}