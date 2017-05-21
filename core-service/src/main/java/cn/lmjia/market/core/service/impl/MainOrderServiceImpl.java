package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.repository.AgentLevelRepository;
import cn.lmjia.market.core.repository.CustomerRepository;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.service.CustomerService;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.ReadService;
import me.jiangcai.wx.model.Gender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
@Service
public class MainOrderServiceImpl implements MainOrderService {

    private static final Log log = LogFactory.getLog(MainOrderServiceImpl.class);

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

    @Override
    public Specification<MainOrder> search(String orderId, String mobile, Long goodId, LocalDate orderDate
            , OrderStatus status) {
        return new Specification<MainOrder>() {
            @Override
            public Predicate toPredicate(Root<MainOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.isTrue(cb.literal(true));
                if (!StringUtils.isEmpty(orderId)) {
                    log.debug("search order with orderId:" + orderId);
                    //前面8位是 时间
                    String ymd = orderId.substring(0, 8);
                    predicate = cb.and(predicate, cb.equal(root.get("dailySerialId"), NumberUtils.parseNumber(orderId.substring(8), Integer.class)));
                    predicate = cb.and(predicate, cb.equal(root.get("orderTime"), LocalDate.from(MainOrder.dateTimeFormatter.parse(ymd))));
                } else if (orderDate != null) {
                    log.debug("search order with orderId:" + orderDate);
                    predicate = cb.and(predicate, cb.equal(root.get("orderTime"), orderDate));
                }
                if (mobile != null) {
                    log.debug("search order with mobile:" + mobile);
                    predicate = cb.and(predicate, cb.like(ReadService.mobileForLogin(MainOrder.getLogin(root), cb), "%" + mobile + "%"));
                }
                if (goodId != null) {
                    predicate = cb.and(predicate, cb.equal(root.get("good").get("id"), goodId));
                }
                if (status != null && status != OrderStatus.EMPTY) {
                    predicate = cb.and(predicate, cb.equal(root.get("orderStatus"), status));
                }

                return predicate;
            }
        };
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
