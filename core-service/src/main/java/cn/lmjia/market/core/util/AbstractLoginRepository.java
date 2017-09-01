package cn.lmjia.market.core.util;

import cn.lmjia.market.core.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface AbstractLoginRepository<T extends Login> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    T findByLoginName(String name);

    List<T> findBySuccessOrderTrue();

    long countByGuideUser(Login guide);

    List<T> findByGuideUserAndSuccessOrderTrue(Login guide);

}
