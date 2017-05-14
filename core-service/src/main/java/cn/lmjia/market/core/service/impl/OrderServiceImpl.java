package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Order;
import cn.lmjia.market.core.entity.ProductType;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.repository.AgentLevelRepository;
import cn.lmjia.market.core.repository.CustomerRepository;
import cn.lmjia.market.core.repository.OrderRepository;
import cn.lmjia.market.core.service.CustomerService;
import cn.lmjia.market.core.service.OrderService;
import me.jiangcai.wx.model.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress, ProductType product, int amount, String mortgageIdentifier) {
        // 客户处理
        Customer customer = customerService.getNoNullCustomer(name, mobile, lowestAgentLevel(who), recommendBy);

        customer.setInstallAddress(installAddress);
        customer.setGender(gender);
        customer.setBirthYear(LocalDate.now().getYear() - age);

        Order order = new Order();
        order.setAmount(amount);
        order.setCustomer(customer);
        order.setInstallAddress(installAddress);
        order.setMortgageIdentifier(mortgageIdentifier);
        order.setOrderBy(who);
        order.setRecommendBy(recommendBy);
        order.setOrderTime(LocalDateTime.now());
        order.setProduct(product);
        order.makeRecord();
        return orderRepository.save(order);
    }

    /**
     * 这个身份相关的经销商；如果登录者并非任何体系内的代理商；则以客户关系查找它所属的经销商
     *
     * @param who 身份
     * @return 经销商
     */
    private AgentLevel lowestAgentLevel(Login who) {
        List<AgentLevel> allAgent = agentLevelRepository.findByLogin(who);

        if (allAgent.isEmpty()) {
            Customer customer = customerRepository.findByLogin(who);
            if (customer == null)
                throw new IllegalStateException("找不到" + who + "所处的经销商");
            return customer.getAgentLevel();
        }

        // 排除掉所有
        AgentLevel[] all = new AgentLevel[allAgent.size()];
        allAgent.toArray(all);

        for (AgentLevel agentLevel : all) {
            // 有人以agentLevel为上级?
            allAgent.stream()
                    .filter(level
                            -> level.getSuperior() == agentLevel)
                    .findAny()
                    .ifPresent(level
                            -> allAgent.remove(agentLevel));
        }

        return allAgent.get(0);
    }
}
