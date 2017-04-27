package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface LoginRepository extends JpaRepository<Login, Long>, JpaSpecificationExecutor<Login> {
}
