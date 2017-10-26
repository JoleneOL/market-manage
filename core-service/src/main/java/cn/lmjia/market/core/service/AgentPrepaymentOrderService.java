package cn.lmjia.market.core.service;

import cn.lmjia.market.core.aop.BusinessLocker;
import cn.lmjia.market.core.aop.BusinessSafe;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.wx.model.Gender;

import java.util.Map;

/**
 * @author CJ
 */
public interface AgentPrepaymentOrderService extends MainDeliverableOrderService<AgentPrepaymentOrder> {

    // 内部API
    @BusinessSafe
    AgentPrepaymentOrder newOrder(BusinessLocker locker, Login who, Login recommendBy, String name, String mobile
            , int age, Gender gender, Address installAddress
            , Map<MainGood, Integer> amounts, String mortgageIdentifier) throws MainGoodLowStockException;
}
