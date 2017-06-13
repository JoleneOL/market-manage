package cn.lmjia.market.core.repository.cache;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.cache.LoginRelation;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

/**
 * @author CJ
 */
public interface LoginRelationRepository extends JpaRepository<LoginRelation, Long>, JpaSpecificationExecutor<LoginRelation> {
    /**
     * 删除一个代理系统的关系缓存
     */
    long deleteBySystem(AgentSystem system);

    long deleteByFromAndTo(Login from, Login to);

    long countBySystem(AgentSystem system);

    List<LoginRelation> findBySystem(AgentSystem system);

    Set<LoginRelation> findBySystemAndTo(AgentSystem system, Login login);

    Set<LoginRelation> findByToAndLevel(Login to, int level);

    List<LoginRelation> findBySystemAndFromAndToAndLevel(AgentSystem system, Login from, Login to, int level);
}
