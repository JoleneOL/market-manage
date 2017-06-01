package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import cn.lmjia.market.core.repository.CustomerRepository;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.service.ContactWayService;
import cn.lmjia.market.core.service.CustomerService;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.cache.LoginRelationCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private LoginService loginService;
    @Autowired
    private LoginRelationCacheService loginRelationCacheService;
    @Autowired
    private ContactWayService contactWayService;

    @Override
    public Customer getNoNullCustomer(String name, String mobile, AgentLevel agentLevel, Login recommendBy) {
        Customer customer = customerRepository.findByNameAndMobile(name, mobile);
        if (customer != null)
            return customer;

        Login customerLogin = loginRepository.findByLoginName(mobile);
        if (customerLogin == null) {
            customerLogin = loginService.newLogin(mobile, recommendBy, "123456");
            // 默认密码是 123456
            contactWayService.updateName(customerLogin, name);
            contactWayService.updateMobile(customerLogin, mobile);
//            contactWayService.u
        }
        customer = new Customer();
        customer.setupAgentLevel(agentLevel);
        customer.setLogin(customerLogin);
        customer.setName(name);
        customer.setMobile(mobile);
        customer = customerRepository.save(customer);
        loginRelationCacheService.addCustomerCache(customer);
        return customer;
    }

    @Override
    public void orderFinish(MainOrderFinishEvent event) {
        Customer customer = event.getMainOrder().getCustomer();
        customer = customerRepository.getOne(customer.getId());
        customer.setSuccessOrder(true);
    }
}
