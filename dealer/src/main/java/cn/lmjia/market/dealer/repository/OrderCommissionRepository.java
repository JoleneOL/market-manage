package cn.lmjia.market.dealer.repository;

import cn.lmjia.market.dealer.entity.OrderCommission;
import cn.lmjia.market.dealer.entity.pk.OrderCommissionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface OrderCommissionRepository extends JpaRepository<OrderCommission, OrderCommissionPK>
        , JpaSpecificationExecutor<OrderCommission> {

}
