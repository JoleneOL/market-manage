package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.Factory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface FactoryRepository extends JpaRepository<Factory, Long>, JpaSpecificationExecutor<Factory> {
    List<Factory> findByEnableTrue();
}
