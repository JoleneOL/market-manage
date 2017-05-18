package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.repository.ManagerRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class InitServiceTest extends CoreServiceTest {

    @Autowired
    private ManagerRepository managerRepository;

    @Test
    public void init() throws Exception {
        Manager root = managerRepository.findByLoginName("root");
        System.out.println(root);
        assertThat(root)
                .isNotNull();
    }

}