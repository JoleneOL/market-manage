package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.repository.AgentLevelRepository;
import cn.lmjia.market.core.repository.CustomerRepository;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.service.CustomerService;
import cn.lmjia.market.core.service.MainOrderService;
import me.jiangcai.wx.model.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
@Service
public class MainOrderServiceImpl implements MainOrderService {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Override
    public MainOrder newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress, MainGood good, int amount, String mortgageIdentifier) {
        // 客户处理
        Customer customer = customerService.getNoNullCustomer(name, mobile, lowestAgentLevel(who), recommendBy);

        customer.setInstallAddress(installAddress);
        customer.setGender(gender);
        customer.setBirthYear(LocalDate.now().getYear() - age);

        MainOrder order = new MainOrder();
        order.setAmount(amount);
        order.setCustomer(customer);
        order.setInstallAddress(installAddress);
        order.setMortgageIdentifier(mortgageIdentifier);
        order.setOrderBy(who);
        order.setRecommendBy(recommendBy);
        order.setOrderTime(LocalDateTime.now());
        order.setGood(good);
        order.makeRecord();
        return mainOrderRepository.save(order);
    }

    @Override
    public MainOrder getOrder(long id) {
        return mainOrderRepository.getOne(id);
    }

    @Override
    public boolean isPaySuccess(long id) {
        return mainOrderRepository.getOne(id).isPay();
//        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Boolean> criteriaQuery = criteriaBuilder.createQuery(Boolean.class);
//        Root<MainOrder> root = criteriaQuery.from(MainOrder.class);
//        criteriaQuery = criteriaQuery.select(root.get("pay"));
//        criteriaQuery = criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
//        return entityManager.createQuery(criteriaQuery).getSingleResult();
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
