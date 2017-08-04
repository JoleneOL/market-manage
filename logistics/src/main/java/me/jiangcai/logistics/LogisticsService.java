package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.Distribution;

import java.util.Collection;

/**
 * @author CJ
 */
public interface LogisticsService {

    //Distribution resource planning

    /**
     * 开启配送
     *
     * @param supplier    物流供应商
     * @param things      需配送的货品
     * @param source      来源地址，可能是供应商，仓库
     * @param destination 目的地
     * @return 配送
     */
    Distribution makeDistribution(LogisticsSupplier supplier, Collection<Thing> things, Source source
            , Destination destination);

    /**
     * 开启配送，和安装一体；并非所有物流都支持
     *
     * @param supplier    物流供应商
     * @param things      需配送的货品
     * @param source      来源地址，可能是供应商，仓库
     * @param destination 目的地
     * @return 配送
     * @see me.jiangcai.logistics.option.LogisticsOptions#Installation
     */
    Distribution makeDistributionWithInstallation(LogisticsSupplier supplier, Collection<Thing> things, Source source
            , Destination destination);

}
