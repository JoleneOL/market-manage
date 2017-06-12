package cn.lmjia.market.dealer.repository;

import cn.lmjia.market.dealer.entity.Commission;
import cn.lmjia.market.dealer.entity.OrderCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface CommissionRepository extends JpaRepository<Commission, Long>, JpaSpecificationExecutor<Commission> {

    List<Commission> findByOrderCommission(OrderCommission orderCommission);

}
