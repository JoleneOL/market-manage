package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.MainOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface OrderRepository extends JpaRepository<MainOrder, Long>, JpaSpecificationExecutor<MainOrder> {
}
