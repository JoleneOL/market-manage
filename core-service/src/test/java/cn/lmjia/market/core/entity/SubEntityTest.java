package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.CoreServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

/**
 * @author CJ
 */
//@ActiveProfiles("mysql")
public class SubEntityTest extends CoreServiceTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    @Rollback(false)
    public void go() {

        SubEntity entity = new SubEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setP1(UUID.randomUUID().toString());
        entity.setP2(UUID.randomUUID().toString());
        entityManager.merge(entity);
    }

}