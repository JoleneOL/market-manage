package cn.lmjia.market.core.repository.deal;

import cn.lmjia.market.core.entity.deal.Salesman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface SalesmanRepository extends JpaRepository<Salesman, Long>, JpaSpecificationExecutor<Salesman> {
}
