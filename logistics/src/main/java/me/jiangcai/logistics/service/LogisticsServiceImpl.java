package me.jiangcai.logistics.service;

import me.jiangcai.logistics.Destination;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.Source;
import me.jiangcai.logistics.Storage;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Distribution;
import me.jiangcai.logistics.option.LogisticsOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author CJ
 */
@Service
public class LogisticsServiceImpl implements LogisticsService {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Distribution makeDistribution(LogisticsSupplier supplier, Collection<Thing> things, Source source
            , Destination destination) {
        return makeDistribution(supplier, things, source, destination, false);
    }

    private Distribution makeDistribution(LogisticsSupplier supplier, Collection<Thing> things, Source source
            , Destination destination, boolean installation) {
        // 不同的供应商可能对于地址有不同的要求
        if (supplier == null) {
            supplier = applicationContext.getBean(LogisticsSupplier.class);
        }
        // 如果Source是个仓库 则表示出库
        int options = (source instanceof Storage) ? LogisticsOptions.CargoFromStorage : 0;
        if (installation)
            options = options | LogisticsOptions.Installation;
        return supplier.makeDistributionOrder(source, things, destination, options);
    }

    @Override
    public Distribution makeDistributionWithInstallation(LogisticsSupplier supplier, Collection<Thing> things, Source source, Destination destination) {
        return makeDistribution(supplier, things, source, destination, true);
    }
}
