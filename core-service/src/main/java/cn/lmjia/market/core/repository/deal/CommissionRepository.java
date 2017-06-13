package cn.lmjia.market.core.repository.deal;

import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.entity.deal.OrderCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface CommissionRepository extends JpaRepository<Commission, Long>, JpaSpecificationExecutor<Commission> {

    List<Commission> findByOrderCommission(OrderCommission orderCommission);

}
