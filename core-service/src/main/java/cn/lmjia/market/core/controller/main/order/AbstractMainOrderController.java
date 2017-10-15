package cn.lmjia.market.core.controller.main.order;

import cn.lmjia.market.core.entity.MainOrder;
import org.springframework.util.StringUtils;

/**
 * @author CJ
 */

public abstract class AbstractMainOrderController extends AbstractMainDeliverableOrderController<MainOrder> {

    protected MainOrder from(String orderId, Long id) {
        if (id != null)
            return mainOrderService.getOrder(id);
        if (!StringUtils.isEmpty(orderId))
            return mainOrderService.getOrder(orderId);
        return null;
    }
}
