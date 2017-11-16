package cn.lmjia.market.dealer.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.rows.AgentPrepaymentOrderRows;
import cn.lmjia.market.core.rows.MainOrderRows;
import cn.lmjia.market.core.service.AgentPrepaymentOrderService;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.util.ApiDramatizer;
import cn.lmjia.market.dealer.service.AgentService;
import cn.lmjia.market.dealer.service.CommissionSettlementService;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.lib.spring.data.AndSpecification;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 订单相关的数据服务
 *
 * @author CJ
 */
@Controller
public class OrderDataController {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.CHINA);
    private static final Log log = LogFactory.getLog(OrderDataController.class);
    @Autowired
    private ReadService readService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private AgentPrepaymentOrderService agentPrepaymentOrderService;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private CommissionSettlementService commissionSettlementService;

    /**
     * @return 仅仅显示我的订单
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/orderList")
    @RowCustom(distinct = true, dramatizer = ApiDramatizer.class)
    public RowDefinition myOrder(@AuthenticationPrincipal Login login, String search, OrderStatus status) {
        return new MainOrderRows(login, t -> t.format(formatter)) {
            @Override
            public Specification<MainOrder> specification() {
                return new AndSpecification<>(
                        mainOrderService.search(search, status)
                        , (root, query, cb) -> cb.equal(root.get("orderBy"), login)
                );
            }
        };
    }

    // 需权限校验
    @PutMapping("/orderData/settlement/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void mockPay(@AuthenticationPrincipal Login login, @PathVariable("id") long id) {
        MainOrder order = mainOrderService.getOrder(id);
        log.info(readService.nameForPrincipal(login) + "尝试重新结算订单" + order.getSerialId());
        commissionSettlementService.reSettlement(order);
    }


    /**
     * 仅仅处理自己可以管辖的订单
     * 即属于我方代理体系的
     */
    @RequestMapping(method = RequestMethod.GET, value = "/orderData/manageableList")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition manageableList(@AuthenticationPrincipal Login login, String orderId
            , @RequestParam(value = "phone", required = false) String mobile, Long goodId
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate beginDate
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate endDate
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate orderDate
            , OrderStatus status) {
        return new MainOrderRows(login, t -> conversionService.convert(t, String.class)) {
            @Override
            public Specification<MainOrder> specification() {
                return new AndSpecification<>(
                        mainOrderService.search(orderId, mobile, goodId, orderDate, beginDate, endDate, status)
                        , agentService.manageableOrder(login)
                );
            }
        };
    }


    @RequestMapping(method = RequestMethod.GET, value = "/orderData2/manageableList")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition manageableList2(@AuthenticationPrincipal Login login, String orderId
            , @RequestParam(value = "phone", required = false) String mobile, Long goodId
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate beginDate
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate endDate
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate orderDate
            , OrderStatus status) {
        return new AgentPrepaymentOrderRows(t -> conversionService.convert(t, String.class)) {
            @Override
            public Specification<AgentPrepaymentOrder> specification() {
                return new AndSpecification<>(
                        agentPrepaymentOrderService.search(orderId, mobile, goodId, orderDate, beginDate, endDate, status)
                        , login.isManageable() ? null : (Specification<AgentPrepaymentOrder>) (root, query, cb) -> cb.disjunction()
                );
            }
        };
    }

}
