package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.service.QuickTradeService;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class QuickTradeServiceImpl implements QuickTradeService {
    @Override
    public void makeDone(MainOrder order) {
        if (order.getOrderStatus() == OrderStatus.afterSale) {
            // 已经进入售后了
        }
        if (order.isPay()) {
            // 只接受已支付的订单,将产生事件
            // dealer 项目独立处理分佣核算已经相关实体
            // 按代理以及身份 生成分佣表 rate为0 将不产生
        }
    }
}
