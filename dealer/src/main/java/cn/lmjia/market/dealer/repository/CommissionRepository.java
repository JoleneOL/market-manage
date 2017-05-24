package cn.lmjia.market.dealer.repository;

import cn.lmjia.market.dealer.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface CommissionRepository extends JpaRepository<Commission, Long>, JpaSpecificationExecutor<Commission> {
}
