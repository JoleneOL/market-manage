package cn.lmjia.market.core.repository.deal;

import cn.lmjia.market.core.entity.deal.OrderCommission;
import cn.lmjia.market.core.entity.deal.pk.OrderCommissionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface OrderCommissionRepository extends JpaRepository<OrderCommission, OrderCommissionPK>
        , JpaSpecificationExecutor<OrderCommission> {

    List<OrderCommission> findBySource(long id);
}
