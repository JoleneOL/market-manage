package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.financing.AgentGoodAdvancePayment;
import cn.lmjia.market.core.entity.financing.AgentGoodAdvancePayment_;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.service.AgentFinancingService;
import cn.lmjia.market.core.service.ReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 管理代理商的预付货款
 *
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_FINANCE + "','" + Login.ROLE_AllAgent + "','" + Login.ROLE_SERVICE + "')")
public class ManageAgentGoodAdvancePaymentController {

    @Autowired
    private AgentFinancingService agentFinancingService;
    @Autowired
    private ConversionService conversionService;

    @GetMapping("/agentGoodAdvancePaymentManage")
    public String index() {
        return "_agentGoodAdvancePayment.html";
    }

    @PostMapping("/manage/agentGoodAdvancePayment/reject")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_FINANCE + "')")
    public void reject(@AuthenticationPrincipal Manager manager, long id, String comment) {
        agentFinancingService.rejectGoodPayment(manager, id, comment);
    }

    @PostMapping("/manage/agentGoodAdvancePayment/approval")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_FINANCE + "')")
    public void approval(@AuthenticationPrincipal Manager manager, long id, String comment) {
        agentFinancingService.approvalGoodPayment(manager, id, comment);
    }

    @PostMapping("/manage/agentGoodAdvancePayment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void add(@AuthenticationPrincipal Manager manager, long login, BigDecimal amount
            , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-M-d") LocalDate date, String serial) {
        agentFinancingService.addGoodPayment(manager, login, amount, date, serial);
    }

    /**
     * @param status 0 全部 2 待处理(默认) 3 已拒绝 4 已批准
     * @return
     */
    @GetMapping("/manage/agentGoodAdvancePayment")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition<AgentGoodAdvancePayment> data(@RequestParam(defaultValue = "2") Integer status, String name
            , String mobile, String serial
            , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-M-d") LocalDate orderDate) {
        return new RowDefinition<AgentGoodAdvancePayment>() {
            public Join<AgentGoodAdvancePayment, Login> loginJoin;

            @Override
            public Class<AgentGoodAdvancePayment> entityClass() {
                return AgentGoodAdvancePayment.class;
            }

            @Override
            public List<FieldDefinition<AgentGoodAdvancePayment>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(AgentGoodAdvancePayment.class, "user")
                                .addBiSelect((agentGoodAdvancePaymentRoot, criteriaBuilder) -> {
                                    loginJoin = agentGoodAdvancePaymentRoot.join(AgentGoodAdvancePayment_.login);
                                    return ReadService.nameForLogin(loginJoin, criteriaBuilder);
                                })
                                .build()
                        , Fields.asBasic("amount")
                        , FieldBuilder.asName(AgentGoodAdvancePayment.class, "mobile")
                                .addBiSelect((agentGoodAdvancePaymentRoot, criteriaBuilder)
                                        -> ReadService.mobileForLogin(loginJoin, criteriaBuilder))
                                .build()
                        , Fields.asBasic("approved")
                        , FieldBuilder.asName(AgentGoodAdvancePayment.class, "status")
                                .addSelect(agentGoodAdvancePaymentRoot -> agentGoodAdvancePaymentRoot.get(AgentGoodAdvancePayment_.approved))
                                .addFormat((data, type) -> {
                                    if (data == null)
                                        return "待处理";
                                    boolean rs = (boolean) data;
                                    return rs ? "已批准" : "已拒绝";
                                })
                                .build()
                        , Fields.asBasic("serial")
                        , Fields.asBasic("comment")
                        , FieldBuilder.asName(AgentGoodAdvancePayment.class, "happenTime")
                                .addSelect(agentGoodAdvancePaymentRoot -> agentGoodAdvancePaymentRoot.get(AgentGoodAdvancePayment_.happenTime))
                                .addFormat((data, type) -> conversionService.convert(data, String.class))
                                .build()
                        , FieldBuilder.asName(AgentGoodAdvancePayment.class, "operator")
                                .addBiSelect((agentGoodAdvancePaymentRoot, criteriaBuilder) -> {
                                    Join<?, Manager> loginJoin = agentGoodAdvancePaymentRoot.join(AgentGoodAdvancePayment_.operator, JoinType.LEFT);
                                    return JpaFunctionUtils.ifElse(criteriaBuilder, String.class, loginJoin.isNotNull(), ReadService.nameForLogin(loginJoin, criteriaBuilder), criteriaBuilder.literal("无"));
                                })
                                .build()
                        , FieldBuilder.asName(AgentGoodAdvancePayment.class, "approval")
                                .addBiSelect((agentGoodAdvancePaymentRoot, criteriaBuilder) -> {
                                    Join<?, Manager> loginJoin = agentGoodAdvancePaymentRoot.join(AgentGoodAdvancePayment_.approval, JoinType.LEFT);
                                    return JpaFunctionUtils.ifElse(criteriaBuilder, String.class, loginJoin.isNotNull(), ReadService.nameForLogin(loginJoin, criteriaBuilder), criteriaBuilder.literal("无"));
                                })
                                .build()
                );
            }

            @Override
            public Specification<AgentGoodAdvancePayment> specification() {
                return (root, query, cb) -> {
                    Predicate predicate = cb.conjunction();
                    switch (status) {
                        case 2:
                            predicate = cb.isNull(root.get(AgentGoodAdvancePayment_.approved));
                            break;
                        case 3:
                            predicate = cb.isFalse(root.get(AgentGoodAdvancePayment_.approved));
                            break;
                        case 4:
                            predicate = cb.isTrue(root.get(AgentGoodAdvancePayment_.approved));
                            break;
                        default:
                    }

                    Join<?, Login> loginForm = root.join(AgentGoodAdvancePayment_.login);

                    if (!StringUtils.isEmpty(name)) {
                        predicate = cb.and(predicate, cb.like(ReadService.nameForLogin(loginForm, cb), "%" + name + "%"));
                    }
                    if (!StringUtils.isEmpty(mobile)) {
                        predicate = cb.and(predicate, cb.like(ReadService.mobileForLogin(loginForm, cb), "%" + mobile + "%"));
                    }
                    if (!StringUtils.isEmpty(serial)) {
                        predicate = cb.and(predicate, cb.like(root.get(AgentGoodAdvancePayment_.serial), "%" + serial + "%"));
                    }
                    if (orderDate != null) {
                        predicate = cb.and(predicate, JpaFunctionUtils.dateEqual(cb, root.get(AgentGoodAdvancePayment_.happenTime), orderDate));
                    }

                    return predicate;
                };
            }
        };
    }

}
