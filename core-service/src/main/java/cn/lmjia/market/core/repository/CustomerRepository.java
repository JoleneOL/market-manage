package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    Customer findByNameAndMobile(String name, String mobile);

    Customer findByLogin(Login login);
}
