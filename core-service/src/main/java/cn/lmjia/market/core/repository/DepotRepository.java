package cn.lmjia.market.core.repository;

import me.jiangcai.logistics.entity.Depot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface DepotRepository extends JpaRepository<Depot, Long>, JpaSpecificationExecutor<Depot> {
}
