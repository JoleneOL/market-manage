package me.jiangcai.logstics.haier.service;

import me.jiangcai.logistics.Destination;
import me.jiangcai.logistics.Source;
import me.jiangcai.logistics.Storage;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Distribution;
import me.jiangcai.logistics.option.LogisticsOptions;
import me.jiangcai.logstics.haier.HaierSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Component
public class HaierSupplierImpl implements HaierSupplier {

    private final String gateway;

    @Autowired
    public HaierSupplierImpl(Environment environment) {
        this.gateway = environment.getProperty("haier.gateway.URL", "http://58.56.128.84:9001/EAI/service/VOM/CommonGetWayToVOM/CommonGetWayToVOM");
    }


    @Override
    public Distribution makeDistributionOrder(Source source, Collection<Thing> things, Destination destination, int options) {
        Map<String, Object> parameters = new HashMap<>();

        String id = UUID.randomUUID().toString().replaceAll("-", "");

        parameters.put("orderno", id);
        parameters.put("sourcesn", id);
        if ((options & LogisticsOptions.CargoFromStorage) == LogisticsOptions.CargoFromStorage) {
            parameters.put("ordertype", "3");
            parameters.put("bustype", "2");
        }
        parameters.put("expno", id);// 快递单号：自动分配的快递单号或客户生成的快递单号
        parameters.put("orderdate", LocalDateTime.now().format(formatter));
        parameters.put("storecode", ((Storage) source).getStorageCode());

        parameters.put("province", destination.getProvince());
        parameters.put("city", destination.getCity());
        parameters.put("county", destination.getCountry());
        parameters.put("addr", destination.getDetailAddress());
        parameters.put("name", destination.getConsigneeName());
        parameters.put("mobile", destination.getConsigneeMobile());

        parameters.put("busflag", (options & LogisticsOptions.Installation) == LogisticsOptions.Installation ? "1" : "2");

        List<Map<String, Object>> items = things.stream()
                .map(this::toItemData)
                .collect(Collectors.toList());

        parameters.put("items", items);


        return null;
    }

    private Map<String, Object> toItemData(Thing thing) {
        Map<String, Object> data = new HashMap<>();
        data.put("storagetype", "10");
        data.put("productcode", thing.getProductCode());
        data.put("prodes", thing.getProductName());
        data.put("number", thing.getAmount());
        return data;
    }
}
