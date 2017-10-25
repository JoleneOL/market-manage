package cn.lmjia.market.core.controller;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Login_;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.entity.settlement.AgentGoodAdvancePaymentJournal;
import cn.lmjia.market.core.entity.settlement.AgentGoodAdvancePaymentJournalType;
import cn.lmjia.market.core.entity.settlement.AgentGoodAdvancePaymentJournal_;
import cn.lmjia.market.core.entity.settlement.LoginCommissionJournal;
import cn.lmjia.market.core.model.ApiResult;
import cn.lmjia.market.core.repository.settlement.LoginCommissionJournalRepository;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.row.supplier.Select2Dramatizer;
import cn.lmjia.market.core.service.ContactWayService;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.ReadService;
import me.jiangcai.lib.spring.data.AndSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 身份数据相关控制器
 *
 * @author CJ
 */
@Controller
public class LoginDataController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private LoginCommissionJournalRepository loginCommissionJournalRepository;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private ContactWayService contactWayService;

    /**
     * 公开可用的手机号码可用性校验
     *
     * @param mobile 确认的手机号码
     * @return 可用性
     */
    @GetMapping("/loginData/mobileValidation")
    @ResponseBody
    public boolean mobileValidation(@RequestParam String mobile) {
        return loginService.mobileValidation(mobile);
    }

    @PreAuthorize("!isAnonymous()")
    @GetMapping("/loginData/select2")
    @RowCustom(dramatizer = Select2Dramatizer.class, distinct = true)
    public RowDefinition<Login> searchLoginSelect2(String search, Boolean agent) {
        return searchLogin(search, agent);
    }

    @PutMapping("/login/name/{id}")
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_AllAgent + "','" + Login.ROLE_MANAGER + "')")
    @ResponseBody
    @Transactional
    public ApiResult changeName(@RequestBody String newName, @PathVariable("id") long id) {
        if (!StringUtils.isEmpty(newName)) {
            contactWayService.updateName(loginService.get(id), newName);
            return ApiResult.withOk();
        }
        return ApiResult.withCodeAndMessage(400, "没有有效的用户名", null);
    }

    @PutMapping("/login/mobile/{id}")
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_AllAgent + "','" + Login.ROLE_MANAGER + "')")
    @ResponseBody
    @Transactional
    public ApiResult changeMobile(@RequestBody String mobile, @PathVariable("id") long id) {
        if (!StringUtils.isEmpty(mobile)) {
            contactWayService.updateMobile(loginService.get(id), mobile);
            return ApiResult.withOk();
        }
        return ApiResult.withCodeAndMessage(400, "没有有效的手机号", null);
    }

    @GetMapping(value = "/loginCommissionJournal", produces = "text/html")
    @Transactional(readOnly = true)
    public String journal(long id, @AuthenticationPrincipal Login login, Model model) {
        // 自己只可以查自己的
        if (!login.isManageable() && login.getId() != id)
            throw new AccessDeniedException("不可以查看别人的流水");
        final List<LoginCommissionJournal> list = loginCommissionJournalRepository.findByLoginOrderByHappenTimeAsc(loginService.get(id));
        model.addAttribute("list", list);

        BigDecimal current = BigDecimal.ZERO;
        // 用于保存当时的数据
        Map<String, Money> currentData = new HashMap<>();
        for (LoginCommissionJournal journal : list) {
            current = current.add(journal.getChanged());
            currentData.put(journal.getId(), new Money(current));
        }
        model.addAttribute("currentData", currentData);

        return "mock/journal.html";
    }

    @GetMapping(value = "/agentGoodAdvancePaymentJournal", produces = "application/json")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition<AgentGoodAdvancePaymentJournal> agentGoodAdvancePaymentJournal(long id, @AuthenticationPrincipal Login login) {
        if (!login.isManageable() && login.getId() != id)
            throw new AccessDeniedException("不可以查看别人的流水");
        return new RowDefinition<AgentGoodAdvancePaymentJournal>() {
            @Override
            public Class<AgentGoodAdvancePaymentJournal> entityClass() {
                return AgentGoodAdvancePaymentJournal.class;
            }

            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<AgentGoodAdvancePaymentJournal> root) {
                return Collections.singletonList(
                        criteriaBuilder.asc(root.get(AgentGoodAdvancePaymentJournal_.happenTime))
                );
            }

            @Override
            public List<FieldDefinition<AgentGoodAdvancePaymentJournal>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(AgentGoodAdvancePaymentJournal.class, "orderId")
                                .addSelect(agentGoodAdvancePaymentJournalRoot
                                        -> agentGoodAdvancePaymentJournalRoot.get(AgentGoodAdvancePaymentJournal_.agentPrepaymentOrderId))
                                .build()
                        , FieldBuilder.asName(AgentGoodAdvancePaymentJournal.class, "event")
                                .addSelect(agentGoodAdvancePaymentJournalRoot
                                        -> agentGoodAdvancePaymentJournalRoot.get(AgentGoodAdvancePaymentJournal_.type))
                                .addFormat((data, type) -> {
                                    AgentGoodAdvancePaymentJournalType journalType = (AgentGoodAdvancePaymentJournalType) data;
                                    switch (journalType) {
                                        case payment:
                                            return "increase";
                                        case makeOrder:
                                            return "decrease";
                                        default:
                                            return "other";
                                    }
                                })
                                .build()
                        , FieldBuilder.asName(AgentGoodAdvancePaymentJournal.class, "happenTime")
                                .addFormat((data, type) -> conversionService.convert(data, String.class))
                                .build()
                        , FieldBuilder.asName(AgentGoodAdvancePaymentJournal.class, "changedAbsMoney")
                                .addBiSelect((agentGoodAdvancePaymentJournalRoot, criteriaBuilder)
                                        -> criteriaBuilder.abs(agentGoodAdvancePaymentJournalRoot.get(AgentGoodAdvancePaymentJournal_.changed)))
                                .build()
                        , Fields.asBasic("type")
                );
            }

            @Override
            public Specification<AgentGoodAdvancePaymentJournal> specification() {
                return (root, query, cb) -> cb.equal(root.get(AgentGoodAdvancePaymentJournal_.login).get(Login_.id), id);
            }
        };
    }

    /**
     * 加入时间，
     * 最早下单时间，
     * 总订单金额
     * 手机号码
     * 名字
     *
     * @return 直接发展的下线默认以时间倒序
     */
    @GetMapping(value = "/loginData/subordinate", produces = "application/json")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition<Login> subordinate(long id, String mobile, @AuthenticationPrincipal Login login) {
        if (!login.isManageable() && login.getId() != id)
            throw new AccessDeniedException("不可以查看别人的流水");
        return new RowDefinition<Login>() {
            @Override
            public Class<Login> entityClass() {
                return Login.class;
            }

            @Override
            public List<FieldDefinition<Login>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(Login.class, "name")
                                .addBiSelect(ReadService::nameForLogin)
                                .build()
                        , FieldBuilder.asName(Login.class, "mobile")
                                .addBiSelect(ReadService::mobileForLogin)
                                .build()
                        , FieldBuilder.asName(Login.class, "createdTime")
                                .addFormat((data, type) -> conversionService.convert(data, String.class))
                                .build()
                        , FieldBuilder.asName(Login.class, "earliestOrderTime")
                                .addOwnSelect((root, cb, query) -> {
                                    Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
                                    Root<MainOrder> root1 = subquery.from(MainOrder.class);
                                    subquery = subquery.select(cb.least(root1.get(MainOrder_.orderTime)))
                                            .groupBy(root1.get(MainOrder_.orderBy))
                                            .where(cb.equal(root1.get(MainOrder_.orderBy), root), MainOrder.getOrderPaySuccess(root1, cb));
                                    return cb.selectCase(cb.literal(true))
                                            .when(true, subquery)
                                            .otherwise(subquery);
                                })
                                .addFormat((data, type) -> conversionService.convert(data, String.class))
                                .build()
                        , FieldBuilder.asName(Login.class, "orderTotal")
                                .addOwnSelect((root, cb, query) -> {
                                    Subquery<BigDecimal> subquery = query.subquery(BigDecimal.class);
                                    Root<MainOrder> root1 = subquery.from(MainOrder.class);
                                    subquery = subquery.select(cb.sum(root1.get(MainOrder_.goodTotalPriceAmountIndependent)))
                                            .groupBy(root1.get(MainOrder_.orderBy))
                                            .where(cb.equal(root1.get(MainOrder_.orderBy), root), MainOrder.getOrderPaySuccess(root1, cb));
                                    return cb.selectCase(cb.literal(true))
                                            .when(true, subquery)
                                            .otherwise(subquery);
                                })
                                .addFormat((data, type) -> conversionService.convert(data, String.class))
                                .build()
                );
            }

            @Override
            public Specification<Login> specification() {
                return (root, query, cb) -> {
                    Predicate predicate = cb.and(
                            cb.equal(root.get(Login_.guideUser).get(Login_.id), id)
                            , cb.isTrue(root.get(Login_.successOrder))
                    );
                    if (StringUtils.isEmpty(mobile))
                        return predicate;
                    return cb.and(predicate, cb.like(ReadService.mobileForLogin(root, cb), "%" + mobile + "%"));
                };
            }
        };
    }

    /**
     * 查询所有用户
     *
     * @param search 可能是名字或者电话号码
     * @param agent  是否只获取代理商
     * @return 字段定义
     */
    private RowDefinition<Login> searchLogin(String search, Boolean agent) {
        return new RowDefinition<Login>() {
            @Override
            public Class<Login> entityClass() {
                return Login.class;
            }

            @Override
            public List<FieldDefinition<Login>> fields() {
                return Arrays.asList(Fields.asBasic("id")
                        , FieldBuilder.asName(Login.class, "name")
                                .addBiSelect(ReadService::nameForLogin)
                                .build()
                        , FieldBuilder.asName(Login.class, "mobile")
                                .addBiSelect(ReadService::mobileForLogin)
                                .build()
                );
            }

            @Override
            public Specification<Login> specification() {
                if (StringUtils.isEmpty(search) && agent == null)
                    return null;
                final Specification<Login> agentSpecification = (root, query, cb) -> {
                    final Expression<Integer> levelForLogin = ReadService.agentLevelForLogin(root, cb);
                    return agent ? cb.lessThan(levelForLogin, Customer.LEVEL)
                            : cb.greaterThanOrEqualTo(levelForLogin, Customer.LEVEL);
//                    Subquery<Long> subquery = query.subquery(Long.class);
//                    Root<AgentLevel> agentLevelRoot = subquery.from(AgentLevel.class);
//                    subquery = subquery
//                            .select(cb.count(agentLevelRoot))
//                            .where(cb.equal(agentLevelRoot.get(AgentLevel_.login), root));
//                    return agent ? cb.greaterThan(subquery, 0L) : cb.equal(subquery, 0L);
                };

                if (StringUtils.isEmpty(search)) {
                    return agentSpecification;
                }
//                    return (root, query, cb) -> {
//                        // 必须得有 所以right
//                        Join<Login, ContactWay> contactWayJoin = root.join("contactWay", JoinType.INNER);
//                        return cb.isNotNull(contactWayJoin);
//                    };
                String jpaSearch = "%" + search + "%";
                final Specification<Login> searchSpecification = (root, query, cb) -> {
                    // 必须得有 所以right
                    Join<Login, ContactWay> contactWayJoin = root.join("contactWay", JoinType.LEFT);
                    return cb.or(
                            cb.like(contactWayJoin.get("mobile"), jpaSearch)
                            , cb.like(contactWayJoin.get("name"), jpaSearch)
                            , cb.like(root.get(Login_.loginName), jpaSearch)
                    );
                };
                if (agent == null)
                    return searchSpecification;
                return new AndSpecification<>(searchSpecification, agentSpecification);
            }
        };
    }


}
