package me.jiangcai.logistics.controller;

import me.jiangcai.jpa.JpaUtils;
import me.jiangcai.logistics.DeliverableOrder;
import me.jiangcai.logistics.LogisticsConfig;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.ManuallyOrder;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ProductStatus;
import me.jiangcai.logistics.exception.UnnecessaryShipException;
import me.jiangcai.logistics.model.DeliverableOrderId;
import me.jiangcai.logistics.option.LogisticsOptions;
import me.jiangcai.logistics.repository.DepotRepository;
import me.jiangcai.logistics.repository.ProductRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + LogisticsConfig.ROLE_SHIP + "')")
public class LogisticsController {

    private static final Log log = LogFactory.getLog(LogisticsController.class);
    @Autowired
    private LogisticsService logisticsService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * @return 发货，并且返回发货之后的库存
     */
    @PostMapping("/api/logisticsShip")
    @Transactional
    @ResponseBody
    public Map<String, Object> logisticsShip(DeliverableOrderId orderPK, boolean installation, long depot
            , HttpServletRequest request, @RequestParam(required = false) String orderNumber
            , @RequestParam(required = false) String company, Locale locale) {
        // 获取订单
        Class<? extends Serializable> idClass = JpaUtils.idClassForEntity(orderPK.getType());
        Serializable id;
        if (idClass == String.class)
            id = orderPK.getId().toString();
        else if (Number.class.isAssignableFrom(idClass)) {
            //noinspection unchecked
            Class<Number> nc = (Class<Number>) idClass;
            id = NumberUtils.parseNumber(orderPK.getId().toString(), nc);
        } else throw new IllegalArgumentException("unknown id class for " + idClass);
        DeliverableOrder order = entityManager.getReference(orderPK.getType(), id);
        // 仓库
        Depot depotEntity = depotRepository.getOne(depot);
        LogisticsSupplier supplier = applicationContext.getBean(depotEntity.getSupplierClass());

        String msg = supplier.orderNumberRequireMessage(locale);
        if (!StringUtils.isEmpty(msg) && (StringUtils.isEmpty(orderNumber) || StringUtils.isEmpty(company))) {
            return with(302, msg);
        }

        // 手动获取货品
        Collection<Thing> collection = new ArrayList<>();
        String[] gArray = request.getParameterValues("goods[]");
        if (gArray == null || gArray.length == 0) {
            gArray = request.getParameterValues("goods");
        }
        Stream.of(gArray)
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    int index = s.lastIndexOf(',');
                    return new String[]{
                            s.substring(0, index)
                            , s.substring(index + 1, s.length())
                    };
                }).forEach(strings -> collection.add(new Thing() {
            @Override
            public Product getProduct() {
                return productRepository.getOne(strings[0]);
            }

            @Override
            public ProductStatus getProductStatus() {
                return ProductStatus.normal;
            }

            @Override
            public int getAmount() {
                return NumberUtils.parseNumber(strings[1], Integer.class);
            }
        }));

        try {
            StockShiftUnit unit = logisticsService.makeShift(supplier, order, collection, depotEntity
                    , order.getLogisticsDestination(), installation ? LogisticsOptions.Installation : 0);
            if (unit instanceof ManuallyOrder) {
                ((ManuallyOrder) unit).setOrderNumber(orderNumber);
                ((ManuallyOrder) unit).setSupplierCompany(company);
            }
        } catch (UnnecessaryShipException e) {
            log.debug("", e);
            return with(400, e.getMessage());
        }

        // ok 了 计算新的库存表
        return withOk(toData(logisticsService.getDepotInfo(order)));
    }

    private Object toData(Map<Depot, Map<Product, Integer>> info) {
        return info.entrySet().stream()
                .collect(Collectors.toMap(depotMapEntry
                                -> depotMapEntry.getKey().getId()
                        , depotMapEntry -> depotMapEntry.getValue().entrySet().stream()
                                .collect(Collectors.toMap(productIntegerEntry
                                                -> productIntegerEntry.getKey().getCode()
                                        , Map.Entry::getValue))));
    }

    private Map<String, Object> withOk(Object data) {
        Map<String, Object> map = with(200, "ok");
        map.put("data", data);
        return map;
    }

    private Map<String, Object> with(int code, String message) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("resultCode", code);
        map.put("resultMsg", message);
        return map;
    }

}
