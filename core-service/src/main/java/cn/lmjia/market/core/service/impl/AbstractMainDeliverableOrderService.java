package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.order.MainDeliverableOrder;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.service.CustomerService;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.MainDeliverableOrderService;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.MarketStockService;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.wx.model.Gender;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
public abstract class AbstractMainDeliverableOrderService<T extends MainDeliverableOrder>
        implements MainDeliverableOrderService<T> {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private MarketStockService marketStockService;


    @Override
    public T newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress, MainOrderService.Amounts amounts, String mortgageIdentifier) throws MainGoodLowStockException {
        // 客户处理
        Customer customer = customerService.getNoNullCustomer(name, mobile, loginService.lowestAgentLevel(who)
                , recommendBy);
        //检查货品库存数量
        marketStockService.checkGoodStock(amounts.getAmounts());

        customer.setInstallAddress(installAddress);
        customer.setGender(gender);
        final LocalDate now = LocalDate.now();
        customer.setBirthYear(now.getYear() - age);

        T order = newOrder(who, recommendBy);

        order.setAmounts(amounts.getAmounts());
        order.setCustomer(customer);
        order.setInstallAddress(installAddress);
//        order.setMortgageIdentifier(mortgageIdentifier);
        order.setOrderTime(LocalDateTime.now());
//        order.setGood(good);
        order.makeRecord();

        return persistOrder(order, mortgageIdentifier);
    }

    /**
     * @param order              已完成初始化的订单
     * @param mortgageIdentifier 可选的按揭码
     * @return 返回完成持久化的订单
     */
    protected abstract T persistOrder(T order, String mortgageIdentifier);

    /**
     * @param who         下单者
     * @param recommendBy 已经没用的推荐者
     * @return 初始化一个order
     */
    protected abstract T newOrder(Login who, Login recommendBy);


}
