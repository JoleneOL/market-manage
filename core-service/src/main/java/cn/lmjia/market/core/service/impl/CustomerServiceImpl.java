package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.CustomerRepository;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.service.CustomerService;
import cn.lmjia.market.core.service.LoginService;
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

    @Override
    public Customer getNoNullCustomer(String name, String mobile, AgentLevel agentLevel, Login recommendBy) {
        Customer customer = customerRepository.findByNameAndMobile(name, mobile);
        if (customer != null)
            return customer;

        Login customerLogin = loginRepository.findByLoginName(mobile);
        if (customerLogin == null) {
            customerLogin = loginService.newLogin(mobile, recommendBy, "123456");
            // 默认密码是 123456
        }
        customer = new Customer();
        customer.setAgentLevel(agentLevel);
        customer.setLogin(customerLogin);
        customer.setName(name);
        customer.setMobile(mobile);
        return customerRepository.save(customer);
    }
}
