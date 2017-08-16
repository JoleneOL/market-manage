package me.jiangcai.logistics.repository;

import me.jiangcai.logistics.entity.UsageStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface UsageStockRepository extends JpaRepository<UsageStock, Long>, JpaSpecificationExecutor<UsageStock> {
}
