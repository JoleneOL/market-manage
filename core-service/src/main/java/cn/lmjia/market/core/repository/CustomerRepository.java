package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    Customer findByNameAndMobile(String name, String mobile);

    List<Customer> findByLogin(Login login);

    /**
     * @return 该login已完成订单的客户数
     */
    long countByLoginAndSuccessOrderTrue(Login login);

    /**
     * @return 这个代理体系的所有客户
     */
    List<Customer> findByAgentLevel_SystemAndSuccessOrderTrue(AgentSystem system);
}
