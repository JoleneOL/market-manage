package me.jiangcai.logistics.controller;

import me.jiangcai.jpa.JpaUtils;
import me.jiangcai.logistics.DeliverableOrder;
import me.jiangcai.logistics.LogisticsConfig;
import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.LogisticsSupplier;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.ManuallyOrder;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.ProductStatus;
import me.jiangcai.logistics.entity.support.ShiftStatus;
import me.jiangcai.logistics.exception.UnnecessaryShipException;
import me.jiangcai.logistics.model.DeliverableOrderId;
import me.jiangcai.logistics.option.LogisticsOptions;
import me.jiangcai.logistics.repository.DepotRepository;
import me.jiangcai.logistics.repository.ProductRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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


    private String message(Object user) {
        if (user == null)
            return "online changes.";
        if (user instanceof UserDetails) {
            return ((UserDetails) user).getUsername() + " made change.";
        }
        return user.toString() + " made change.";
    }

    @PutMapping("/api/logisticsEventReject/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable("id") long id, @AuthenticationPrincipal Object user) {
        logisticsService.mockToStatus(id, ShiftStatus.reject, message(user));
    }

    @PutMapping("/api/logisticsEventSuccess/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void success(@PathVariable("id") long id, @AuthenticationPrincipal Object user) {
        logisticsService.mockToStatus(id, ShiftStatus.success, message(user));
    }

    @PutMapping("/api/logisticsEventInstall/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void install(@PathVariable("id") long id, String installer, String installCompany, String mobile) {
        logisticsService.mockInstallationEvent(id, installer, installCompany, mobile);
    }

    /**
     * @return 发货，并且返回发货之后的库存
     */
    @PostMapping("/api/logisticsShip")
    @Transactional
    @ResponseBody
    public Map<String, Object> logisticsShip(DeliverableOrderId orderPK, boolean installation, long depot
            , HttpServletRequest request, @RequestParam(required = false) String orderNumber
            , @RequestParam(required = false) String consigneeName
            , @RequestParam(required = false) String consigneeMobile
            , @RequestParam(required = false) String address
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
        final Collection<Thing> collection = new ArrayList<>();
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

        // 获取原来的库存信息
        final Map<Depot, Map<Product, Integer>> depotInfo = logisticsService.getDepotInfo(order);
        // 调整被选择仓库的库存
        depotInfo.computeIfPresent(depotEntity, (depot1, productIntegerMap) -> {
            collection.forEach(thing
                    -> productIntegerMap.computeIfPresent(thing.getProduct()
                    // 将特定货品数量降低
                    , (product, integer) -> integer - thing.getAmount()));
            return productIntegerMap;
        });

        LogisticsDestination defaultDestination = order.getLogisticsDestination();
        // 地址信息必须高度符合规格！
        final String province;
        final String city;
        final String country;
        final String detailAddress;
        if (!StringUtils.isEmpty(address) && address.split("-").length >= 4) {
            String[] _address = address.split("-");
            province = _address[0];
            city = _address[1];
            country = _address[2];
            String[] _detailAddress = new String[_address.length - 3];
            System.arraycopy(_address, 3, _detailAddress, 0, _detailAddress.length);
            detailAddress = String.join("-", _detailAddress);
        } else {
            province = defaultDestination.getProvince();
            city = defaultDestination.getCity();
            country = defaultDestination.getCountry();
            detailAddress = defaultDestination.getDetailAddress();
        }

        LogisticsDestination destination = new LogisticsDestination() {
            @Override
            public String getProvince() {
                return province;
            }

            @Override
            public String getCity() {
                return city;
            }

            @Override
            public String getCountry() {
                return country;
            }

            @Override
            public String getDetailAddress() {
                return detailAddress;
            }

            @Override
            public String getConsigneeName() {
                return !StringUtils.isEmpty(consigneeName) ? consigneeName : defaultDestination.getConsigneeName();
            }

            @Override
            public String getConsigneeMobile() {
                return !StringUtils.isEmpty(consigneeMobile) ? consigneeMobile : defaultDestination.getConsigneeMobile();
            }
        };

        try {
            StockShiftUnit unit = logisticsService.makeShift(supplier, order, collection, depotEntity
                    , destination, installation ? LogisticsOptions.Installation : 0);
            if (unit instanceof ManuallyOrder) {
                ((ManuallyOrder) unit).setOrderNumber(orderNumber);
                ((ManuallyOrder) unit).setSupplierCompany(company);
            }
        } catch (UnnecessaryShipException e) {
            log.debug("", e);
            return with(400, e.getMessage());
        }

        // ok 了 计算新的库存表

        return withOk(toData(depotInfo));
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
