package cn.lmjia.market.manage.controller.trj;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.entity.trj.AuthorisingStatus;
import cn.lmjia.market.core.entity.trj.TRJPayOrder;
import cn.lmjia.market.core.repository.trj.AuthorisingInfoRepository;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.rows.MainOrderRows;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.QuickTradeService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.trj.TRJService;
import me.jiangcai.lib.spring.data.AndSpecification;
import me.jiangcai.payment.entity.PayOrder;
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
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_MANAGER + "')")
public class TRJManageController {

    @Autowired
    private QuickTradeService quickTradeService;
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private TRJService trjService;
    @Autowired
    private AuthorisingInfoRepository authorisingInfoRepository;
    @Autowired
    private Environment environment;

    @PostMapping("/orderData/quickDone/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quickDone(@PathVariable("id") long id, String deliverCompany, String deliverStore, int stockQuantity
            , @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate shipmentTime
            , @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deliverTime) {
        quickTradeService.makeDone(mainOrderService.getOrder(id));
        trjService.deliverUpdate(id, deliverCompany, deliverStore, stockQuantity, shipmentTime, deliverTime);
    }

    //     申请
    @GetMapping("/mortgageTRG")
    public String index() {
        return "_mortgageTRG.html";
    }

    @PostMapping("/mortgageTRGAppeal")
    @Transactional
    public String update(long id, String installer, String installCompany, String mobile
            , @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate installDate, String applyFile) {
        // 资源应该是保存在某个目录下的
        if (applyFile != null)
            applyFile = applyFile.replaceAll(",", "");
        trjService.submitOrderCompleteRequest(mainOrderService.getOrder(id), installer, installCompany, mobile
                , installDate.atStartOfDay(), applyFile);
        return "redirect:/mortgageTRG";
    }

    @GetMapping("/_TRJ_snickerData")
    @PreAuthorize("hasAnyRole('ROOT')")
    @ResponseBody
    public Object snickerData() {
        if (environment.acceptsProfiles(CoreConfig.ProfileUnitTest) || environment.acceptsProfiles("staging"))
            return authorisingInfoRepository.findAll((root, query, cb) -> cb.equal(root.get("authorisingStatus"), AuthorisingStatus.Unused));
        return null;
    }

    @GetMapping("/mortgageTRGAppeal")
    public String appeal(long id, Model model) {
        final MainOrder order = mainOrderService.getOrder(id);
        model.addAttribute("mainOrder", order);
        model.addAttribute("payOrder", order.getPayOrder());
        return "_appeal.html";
    }

    @GetMapping("/manage/mortgage")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition<MainOrder> data(@AuthenticationPrincipal Login login, String orderId
            , String mortgageCode
            , @RequestParam(value = "phone", required = false) String mobile, Long goodId
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate orderDate
            , OrderStatus status) {
        return new MainOrderRows(login, t -> conversionService.convert(t, String.class)) {

            @Override
            public List<FieldDefinition<MainOrder>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(MainOrder.class, "orderId")
                                .addBiSelect((MainOrder::getSerialId))
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "mortgageCode")
                                .addBiSelect(((root, criteriaBuilder)
                                        -> criteriaBuilder.treat(root.join("payOrder"), TRJPayOrder.class)
                                        .join("authorisingInfo").get("id")))
                                .build()
                        , Fields.asBiFunction("userName", ((root, criteriaBuilder)
                                -> ReadService.nameForLogin(MainOrder.getOrderByLogin(root)
                                , criteriaBuilder)))
                        , Fields.asBiFunction("mobile", ((root, criteriaBuilder)
                                -> ReadService.mobileForLogin(MainOrder.getOrderByLogin(root), criteriaBuilder)))
                        , FieldBuilder.asName(MainOrder.class, "orderTime")
                                .addFormat((data, type)
                                        -> orderTimeFormatter.apply(((LocalDateTime) data)))
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "status")
                                .addBiSelect(((root, criteriaBuilder)
                                        -> criteriaBuilder.treat(root.join("payOrder"), TRJPayOrder.class)
                                        .join("authorisingInfo").get("authorisingStatus")))
                                .addFormat((data, type) -> data == null ? null : data.toString())
                                .build()
                        , FieldBuilder.asName(MainOrder.class, "statusCode")
                                .addBiSelect(((root, criteriaBuilder)
                                        -> criteriaBuilder.treat(root.join("payOrder"), TRJPayOrder.class)
                                        .join("authorisingInfo").get("authorisingStatus")))
                                .addFormat((data, type) -> data == null ? null : ((Enum) data).ordinal())
                                .build()
                );
            }

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
}
