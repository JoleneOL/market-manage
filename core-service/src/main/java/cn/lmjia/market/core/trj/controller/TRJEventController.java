package cn.lmjia.market.core.trj.controller;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.entity.trj.AuthorisingInfo;
import cn.lmjia.market.core.entity.trj.AuthorisingStatus;
import cn.lmjia.market.core.entity.trj.TRJPayOrder;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.rows.MainOrderRows;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.QuickTradeService;
import cn.lmjia.market.core.trj.TRJService;
import me.jiangcai.lib.ee.ServletUtils;
import me.jiangcai.lib.spring.data.AndSpecification;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.payment.entity.PayOrder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CJ
 */
@Controller
public class TRJEventController {

    private static final Log log = LogFactory.getLog(TRJEventController.class);
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private Environment environment;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private TRJService trjService;
    @Autowired
    private QuickTradeService quickTradeService;
    @Autowired
    private MainOrderService mainOrderService;

    @PostMapping("/orderData/quickDone/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_MANAGER + "')")
    public void quickDone(@PathVariable("id") long id, String deliverCompany, String deliverStore, int stockQuantity
            , @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate shipmentTime
            , @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deliverTime) {
        quickTradeService.makeDone(mainOrderService.getOrder(id));
        trjService.deliverUpdate(id, deliverCompany, deliverStore, stockQuantity, shipmentTime, deliverTime);
    }

    // 其他几个管理功能
    // 页面 展示数据 申请
    @GetMapping("/mortgageTRG")
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_MANAGER + "')")
    public String index() {
        return "_mortgageTRG.html";
    }

    @GetMapping("/manage/mortgage")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_MANAGER + "')")
    public RowDefinition<MainOrder> data(@AuthenticationPrincipal Login login, String orderId
            , String mortgageCode
            , @RequestParam(value = "phone", required = false) String mobile, Long goodId
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate orderDate
            , OrderStatus status) {
        return new MainOrderRows(login, t -> conversionService.convert(t, String.class)) {

//'id': '@id',
//        'orderId': '@id',
//        'mortgageCode': '@word(5)@integer(100)',
//        'userName': '@cname',
//        'mobile': /^1([34578])\d{9}$/,
//                    'orderTime': '@datetime("yyyy-MM-dd")',
//                    'status': '待订单完成',
//                    'statusCode': 1

            @Override
            public Specification<MainOrder> specification() {
                return new AndSpecification<>(
                        mainOrderService.search(orderId, mobile, goodId, orderDate, status)
                        , (root, query, cb) -> {
                    final Join<MainOrder, PayOrder> payOrder = root.join("payOrder");
                    Predicate predicate = cb.and(
                            cb.isNotNull(payOrder),
                            cb.equal(payOrder.type(), TRJPayOrder.class)
                    );
                    if (StringUtils.isEmpty(mortgageCode))
                        return predicate;
                    return cb.and(predicate
                            , cb.like(cb.treat(payOrder, TRJPayOrder.class)
                                    .join("authorisingInfo").get("id"), "%" + mortgageCode + "%")
                    );
                }
                );
            }
        };
    }


    @PostMapping("/_tourongjia_event_")
    @ResponseBody
    @Transactional
    public Map<String, Object> event(HttpServletRequest request, String event, String authorising, String idNumber
            , String message, @RequestParam(required = false) Boolean result
            , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time) {
        Map<String, Object> apiResult = new HashMap<>();
        apiResult.put("boolen", true);
        apiResult.put("message", "OK");
        apiResult.put("data", null);
        // 安全检查
        securityCheck(ServletUtils.clientIpAddress(request));

        if (event.equalsIgnoreCase("code")) {
            addCode(authorising, idNumber);
            return apiResult;
        }

        if (event.equalsIgnoreCase("v1")) {
            v1Check(authorising, result, message);
            return apiResult;
        }

        if (event.equalsIgnoreCase("v4")) {
            settlement(authorising, time);
            return apiResult;
        }


        throw new IllegalArgumentException();
    }

    private void settlement(String authorising, LocalDateTime time) {
        log.debug("settlement Result:" + authorising + " time:" + time);
        MainOrder order = trjService.findOrder(authorising);
        TRJPayOrder payOrder = (TRJPayOrder) order.getPayOrder();
        final AuthorisingInfo authorisingInfo = payOrder.getAuthorisingInfo();

        if (authorisingInfo.getAuthorisingStatus() != AuthorisingStatus.forSettle)
            throw new IllegalStateException("并未处于等待审核的状态");
        authorisingInfo.setAuthorisingStatus(AuthorisingStatus.settle);
        authorisingInfo.setSettlementTime(time);
        order.setDisableSettlement(false);
    }

    private void v1Check(String authorising, boolean result, String message) {
        log.debug("v1Check Result:" + authorising + " result:" + result + " ,message:" + message);
        MainOrder order = trjService.findOrder(authorising);
        TRJPayOrder payOrder = (TRJPayOrder) order.getPayOrder();
        final AuthorisingInfo authorisingInfo = payOrder.getAuthorisingInfo();
        if (authorisingInfo.getAuthorisingStatus() != AuthorisingStatus.auditing)
            throw new IllegalStateException("并未处于等待审核的状态");
        authorisingInfo.setMessage(message);
        if (result)
            authorisingInfo.setAuthorisingStatus(AuthorisingStatus.forSettle);
        else
            authorisingInfo.setAuthorisingStatus(AuthorisingStatus.auditingRefuse);

        if (authorisingInfo.getAuthorisingStatus() == AuthorisingStatus.auditingRefuse)
            trjService.sendCheckWarningToCS(order, "信审被拒:" + message);
    }

    private void addCode(String authorising, String idNumber) {
        log.debug("add Authorising Code:" + authorising + ":" + idNumber);
        trjService.addAuthorisingInfo(authorising, idNumber);
    }

    private void securityCheck(String ip) {
        // 测试环境 跳过
        if (environment.acceptsProfiles(CoreConfig.ProfileUnitTest) || environment.acceptsProfiles("staging"))
            return;
        String ips = systemStringService.getCustomSystemString("trj.ips", "trj.ips.comment", true, String.class, null);
        if (StringUtils.isEmpty(ips))
            throw new IllegalAccessError("bad access from " + ip);
        if (!Arrays.asList(ips.split(",")).contains(ip))
            throw new IllegalAccessError("bad access from " + ip);
    }

}
