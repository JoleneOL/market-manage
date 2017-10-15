package cn.lmjia.market.core.controller.main.order;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.service.MainOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * @author CJ
 */

public abstract class AbstractMainOrderController extends AbstractMainDeliverableOrderController<MainOrder> {

    @Autowired
    private MainOrderService mainOrderService;

    protected MainOrder from(String orderId, Long id) {
        if (id != null)
            return mainDeliverableOrderService.getOrder(id);
        if (!StringUtils.isEmpty(orderId))
            return mainOrderService.getOrder(orderId);
        return null;
    }
}
