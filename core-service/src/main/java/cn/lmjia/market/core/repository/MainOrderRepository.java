package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.MainOrder;
import me.jiangcai.payment.entity.PayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * @author CJ
 */
public interface MainOrderRepository extends JpaRepository<MainOrder, Long>, JpaSpecificationExecutor<MainOrder> {
}
