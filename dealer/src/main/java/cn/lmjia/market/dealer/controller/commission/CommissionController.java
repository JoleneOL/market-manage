package cn.lmjia.market.dealer.controller.commission;

import cn.lmjia.market.core.converter.LocalDateConverter;
import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.Customer_;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.entity.deal.Commission_;
import cn.lmjia.market.core.entity.deal.OrderCommission;
import cn.lmjia.market.core.entity.deal.OrderCommission_;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.util.ApiDramatizer;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.field.FieldBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * @author CJ
 */
@Controller
public class CommissionController {

    public static String formatCommonInfo(Object origin) {
        String src = origin.toString();
        int index = src.lastIndexOf("￥");
        String first = src.substring(0, index - 1);
        return first + Money.format.format(new BigDecimal(src.substring(index + 1)));
    }

    /**
     * @param login 身份
     * @param type  today,month,previous,quarter,all
     * @return
     */
    @RowCustom(dramatizer = ApiDramatizer.class, distinct = true)
    @GetMapping("/api/commList/{type}")
    public RowDefinition<Commission> commList(@AuthenticationPrincipal Login login, @PathVariable("type") String type) {
        return new RowDefinition<Commission>() {
            @Override
            public Class<Commission> entityClass() {
                return Commission.class;
            }

            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<Commission> root) {
                return Collections.singletonList(
                        criteriaBuilder.desc(root.join(Commission_.orderCommission).get(OrderCommission_.source)
                                .get(MainOrder_.orderTime)));
            }

            @Override
            public Expression<?> count(CriteriaQuery<Long> countQuery, CriteriaBuilder criteriaBuilder, Root<Commission> root) {
                // orderCommission 没有传统主键!
                return OrderCommission.getIdSelection(criteriaBuilder, root.join("orderCommission"));
            }

            @Override
            public List<FieldDefinition<Commission>> fields() {
                return Arrays.asList(
                        new FieldDefinition<Commission>() {
                            @Override
                            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query
                                    , Root<Commission> root) {
                                return OrderCommission.getIdSelection(criteriaBuilder, root.join("orderCommission"));
                            }

                            @Override
                            public String name() {
                                return "id";
                            }

                            @Override
                            public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
                                return origin;
                            }

                            @Override
                            public Expression<?> order(Root<Commission> root, CriteriaBuilder criteriaBuilder) {
                                return null;
                            }
                        }
                        , new FieldDefinition<Commission>() {
                            @Override
                            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<Commission> root) {
                                return criteriaBuilder.literal("销售收益");
                            }

                            @Override
                            public String name() {
                                return "commType";
                            }

                            @Override
                            public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
                                return origin;
                            }

                            @Override
                            public Expression<?> order(Root<Commission> root, CriteriaBuilder criteriaBuilder) {
                                return null;
                            }
                        }
                        , new FieldDefinition<Commission>() {
                            @Override
                            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<Commission> root) {
                                From<?, MainOrder> orderFrom = root.join(Commission_.orderCommission).join(OrderCommission_.source);
                                return JpaFunctionUtils.contact(
                                        criteriaBuilder
                                        , orderFrom.get(MainOrder_.orderBody)
                                        , criteriaBuilder.literal(" ￥")
                                        , MainOrder.getOrderDueAmount(orderFrom, criteriaBuilder).as(String.class)
                                );
                            }

                            @Override
                            public String name() {
                                return "commInfo";
                            }

                            @Override
                            public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
                                return formatCommonInfo(origin);
                            }

                            @Override
                            public Expression<?> order(Root<Commission> root, CriteriaBuilder criteriaBuilder) {
                                return null;
                            }
                        }
                        , new FieldDefinition<Commission>() {
                            @Override
                            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<Commission> root) {
                                return root.join("orderCommission").get("generateTime");
                            }

                            @Override
                            public String name() {
                                return "commTime";
                            }

                            @Override
                            public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
                                LocalDateTime localDateTime = (LocalDateTime) origin;
                                return LocalDateConverter.formatter.format(LocalDate.from(localDateTime));
                            }

                            @Override
                            public Expression<?> order(Root<Commission> root, CriteriaBuilder criteriaBuilder) {
                                return null;
                            }
                        }
                        , FieldBuilder.asName(Commission.class, "name")
                                .addSelect(commissionRoot -> commissionRoot
                                        .get(Commission_.orderCommission).get(OrderCommission_.source).get(MainOrder_.customer).get(Customer_.name))
                                .build()
                        , new FieldDefinition<Commission>() {
                            @Override
                            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<Commission> root) {
                                return criteriaBuilder.sum(root.get("amount"));
                            }

                            @Override
                            public String name() {
                                return "commission";
                            }

                            @Override
                            public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
                                return origin;
                            }

                            @Override
                            public Expression<?> order(Root<Commission> root, CriteriaBuilder criteriaBuilder) {
                                return null;
                            }
                        }
                        , new FieldDefinition<Commission>() {
                            @Override
                            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<Commission> root) {
                                return criteriaBuilder.sum(root.get("rate"));
                            }

                            @Override
                            public String name() {
                                return "divided";
                            }

                            @Override
                            public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
                                NumberFormat format = NumberFormat.getPercentInstance(Locale.CHINA);
                                format.setMaximumFractionDigits(2);
                                return format.format(origin);
                            }

                            @Override
                            public Expression<?> order(Root<Commission> root, CriteriaBuilder criteriaBuilder) {
                                return null;
                            }
                        }
                );
            }

            @Override
            public Specification<Commission> specification() {
                if ("pending".equals(type))
                    return Commission.listAllSpecification(login, (root, query, cb)
                            -> Commission.reality(root, cb).not());
                if ("weekPending".equals(type)) {
                    return Commission.listAllSpecification(login,new Specification<Commission>() {
                        @Override
                        public Predicate toPredicate(Root<Commission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                            return cb.and(
                                    cb.isTrue(root.get(Commission_.orderCommission).get(OrderCommission_.pending))
                                    , cb.isFalse(root.get(Commission_.orderCommission).get(OrderCommission_.source).get(MainOrder_.disableSettlement))
                                    , JpaFunctionUtils.ymd(cb, root.get("orderCommission").get("generateTime")
                                            , LocalDate.now()
                                            , ((criteriaBuilder, integerExpression, integerExpression2) -> {
                                                return criteriaBuilder.greaterThanOrEqualTo(integerExpression, criteriaBuilder.diff(integerExpression2, 7));
                                            })));
                        }
                    });
                }
                if ("all".equals(type))
                    return Commission.listRealitySpecification(login, null);
                return Commission.listRealitySpecification(login, (root, query, cb) -> {
                    if ("today".equals(type))
                        return JpaFunctionUtils.dateEqual(cb, root.get("orderCommission").get("generateTime")
                                , LocalDate.now());
                    if ("month".equals(type))
                        return JpaFunctionUtils.yearAndMonthEqual(cb, root.get("orderCommission").get("generateTime")
                                , LocalDate.now());
                    if ("previous".equals(type))
                        return JpaFunctionUtils.yearAndMonthEqual(cb, root.get("orderCommission").get("generateTime")
                                , LocalDate.now().minusMonths(1));
                    if ("quarter".equals(type))
                        return JpaFunctionUtils.ym(cb, root.get("orderCommission").get("generateTime")
                                , LocalDate.now()
                                , (criteriaBuilder, integerExpression, integerExpression2) -> {
                                    // >= ym-3
                                    return criteriaBuilder.greaterThanOrEqualTo(integerExpression,
                                            criteriaBuilder.sum(integerExpression2, -3));
                                });
                    if ("week".equals(type)) {
                        return JpaFunctionUtils.ymd(cb, root.get("orderCommission").get("generateTime")
                                , LocalDate.now()
                                , ((criteriaBuilder, integerExpression, integerExpression2) -> {
                                    return criteriaBuilder.greaterThanOrEqualTo(integerExpression, criteriaBuilder.diff(integerExpression2, 7));
                                }));
                    }
                    throw new IllegalArgumentException("未知的查询类型:" + type);
                });
            }
        };
    }

}
