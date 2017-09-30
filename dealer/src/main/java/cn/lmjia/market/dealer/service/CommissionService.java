package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.deal.Commission;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 佣金记录服务
 */
public interface CommissionService {
    /**
     * @param id    订单id
     * @return  改订单所有的佣金记录
     */
    List<Commission> findByOrderId(long id);
}
