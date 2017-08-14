package cn.lmjia.market.core.repository.trj;

import cn.lmjia.market.core.entity.trj.AuthorisingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface AuthorisingInfoRepository extends JpaRepository<AuthorisingInfo, String>
        , JpaSpecificationExecutor<AuthorisingInfo> {
}
