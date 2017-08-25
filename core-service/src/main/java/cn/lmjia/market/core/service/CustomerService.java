package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.event.LoginRelationChangedEvent;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import me.jiangcai.lib.thread.ThreadSafe;
import me.jiangcai.payment.event.OrderPaySuccess;
import org.springframework.context.event.EventListener;

/**
 * 客户相关服务
 *
 * @author CJ
 */
public interface CustomerService {

    /**
     * 获取现有客户，或者新增客户
     *
     * @param name        姓名
     * @param mobile      电话号码
     * @param agentLevel  所属经销商，必然是代理体系的最低端
     * @param recommendBy 推荐用户
     * @return 必然非null的客户
     */
    Customer getNoNullCustomer(String name, String mobile, AgentLevel agentLevel, Login recommendBy);

    /**
     * 客户也需要知道这个事情
     *
     * @param event e
     * @return 是否产生新的事件
     */
    @EventListener(MainOrderFinishEvent.class)
    @ThreadSafe
    LoginRelationChangedEvent orderFinish(MainOrderFinishEvent event);

    /**
     * 客户也需要知道这个事情
     *
     * @param event e
     * @return 是否产生新的事件
     */
    @EventListener(OrderPaySuccess.class)
    @ThreadSafe
    LoginRelationChangedEvent orderPay(OrderPaySuccess event);
}
