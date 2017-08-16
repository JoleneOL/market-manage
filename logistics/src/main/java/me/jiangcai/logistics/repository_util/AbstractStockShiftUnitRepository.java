package me.jiangcai.logistics.repository_util;

import me.jiangcai.logistics.entity.StockShiftUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface AbstractStockShiftUnitRepository<T extends StockShiftUnit> extends JpaRepository<T, Long>
        , JpaSpecificationExecutor<T> {


}
