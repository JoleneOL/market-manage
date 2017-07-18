package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Distribution;

import java.util.Collection;

/**
 * 物流服务供应商
 *
 * @author CJ
 */
public interface LogisticsSupplier {

    Distribution makeDistributionOrder(Source source, Collection<Thing> things, Destination destination, int options);
}
