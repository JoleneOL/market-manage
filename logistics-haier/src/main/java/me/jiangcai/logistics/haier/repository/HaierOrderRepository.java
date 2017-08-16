package me.jiangcai.logistics.haier.repository;

import me.jiangcai.logistics.haier.entity.HaierOrder;
import me.jiangcai.logistics.repository_util.AbstractStockShiftUnitRepository;

/**
 * @author CJ
 */
public interface HaierOrderRepository extends AbstractStockShiftUnitRepository<HaierOrder> {

    HaierOrder findByOrderNumber(String no);

}
