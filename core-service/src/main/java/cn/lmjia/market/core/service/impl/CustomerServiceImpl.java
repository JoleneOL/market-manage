package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.event.LoginRelationChangedEvent;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import cn.lmjia.market.core.repository.CustomerRepository;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.service.CustomerService;
import cn.lmjia.market.core.service.cache.LoginRelationCacheService;
import me.jiangcai.payment.event.OrderPaySuccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Log log = LogFactory.getLog(CustomerServiceImpl.class);
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private LoginRelationCacheService loginRelationCacheService;

    @Override
    public Customer getNoNullCustomer(String name, String mobile, AgentLevel agentLevel, Login recommendBy) {
        Customer customer = customerRepository.findByNameAndMobile(name, mobile);
        if (customer != null)
            return customer;

//        Login customerLogin = loginRepository.findByLoginName(mobile);
//        if (customerLogin == null) {
//            customerLogin = loginService.newLogin(Login.class, mobile, recommendBy, "123456");
//            // 默认密码是 123456
//            contactWayService.updateName(customerLogin, name);
//            contactWayService.updateMobile(customerLogin, mobile);
////            contactWayService.u
//        }
        customer = new Customer();
//        customer.setupAgentLevel(agentLevel);
//        customer.setLogin(customerLogin);
        customer.setName(name);
        customer.setMobile(mobile);
        customer = customerRepository.save(customer);
        return customer;
    }

    @Override
    public LoginRelationChangedEvent orderFinish(MainOrderFinishEvent event) {
        MainOrder mainOrder = event.getMainOrder();
        return forSuccessMainOrder(mainOrder);
    }

    private LoginRelationChangedEvent forSuccessMainOrder(MainOrder mainOrder) {
//        Customer customer = mainOrder.getCustomer();
        Login login = loginRepository.getOne(mainOrder.getOrderBy().getId());
        if (!login.isSuccessOrder()) {
            log.debug(login + "首付付费成功，成为【爱心天使】");
            login.setSuccessOrder(true);
            loginRelationCacheService.loginFirstOrder(login);
            // 如果它有推荐则产生改变
            if (login.getGuideUser() != null)
                return new LoginRelationChangedEvent(login.getGuideUser());
        }
        return null;
    }

    @Override
    public LoginRelationChangedEvent orderPay(OrderPaySuccess event) {
        if (event.getPayableOrder() instanceof MainOrder) {
            return forSuccessMainOrder((MainOrder) event.getPayableOrder());
        }
        return null;
    }
}
