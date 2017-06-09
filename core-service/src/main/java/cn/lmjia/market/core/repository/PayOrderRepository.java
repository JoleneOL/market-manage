package cn.lmjia.market.core.repository;

import me.jiangcai.payment.entity.PayOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface PayOrderRepository extends JpaRepository<PayOrder, Long> {

    List<PayOrder> findByPayableOrderId(String id);

}
