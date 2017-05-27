package cn.lmjia.market.dealer.controller.commission;

import cn.lmjia.market.core.converter.LocalDateConverter;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.util.ApiDramatizer;
import cn.lmjia.market.dealer.entity.Commission;
import cn.lmjia.market.dealer.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * @author CJ
 */
@Controller
public class CommissionController {

    @Autowired
    private CommissionService commissionService;

    /**
     * @param login 身份
     * @param type  today,month,previous,quarter
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
            public Expression<?> count(CriteriaBuilder criteriaBuilder, Root<Commission> root) {
                // orderCommission 没有传统主键!
                return criteriaBuilder.concat(
                        criteriaBuilder.concat(
                                root.get("orderCommission").get("source").get("id").as(String.class)
                                , "-")
                        , root.get("orderCommission").get("refund")
                );
            }

            @Override
            public List<FieldDefinition<Commission>> fields() {
                return Arrays.asList(
                        new FieldDefinition<Commission>() {
                            @Override
                            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query
                                    , Root<Commission> root) {
                                return criteriaBuilder.concat(
                                        criteriaBuilder.concat(
                                                root.get("orderCommission").get("source").get("id").as(String.class)
                                                , "-")
                                        , root.get("orderCommission").get("refund")
                                );
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
                                From<?, MainOrder> orderFrom = root.join("orderCommission").join("source");
                                return JpaFunctionUtils.Contact(
                                        criteriaBuilder
                                        , orderFrom.get("amount")
                                        , criteriaBuilder.literal("个")
                                        , orderFrom.get("good").get("product").get("name")
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
                        , new FieldDefinition<Commission>() {
                            @Override
                            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<Commission> root) {
                                return ReadService.nameForCustomer(root.join("orderCommission").join("source").join("customer"), criteriaBuilder);
                            }

                            @Override
                            public String name() {
                                return "name";
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
                return commissionService.listSpecification(login, (root, query, cb) -> {
                    if ("today".equals(type))
                        return JpaFunctionUtils.DateEqual(cb, root.get("orderCommission").get("generateTime")
                                , LocalDate.now());
                    if ("month".equals(type))
                        return JpaFunctionUtils.YearAndMonthEqual(cb, root.get("orderCommission").get("generateTime")
                                , LocalDate.now());
                    if ("previous".equals(type))
                        return JpaFunctionUtils.YearAndMonthEqual(cb, root.get("orderCommission").get("generateTime")
                                , LocalDate.now().minusMonths(1));
                    if ("quarter".equals(type))
                        return JpaFunctionUtils.YM(cb, root.get("orderCommission").get("generateTime")
                                , LocalDate.now()
                                , (criteriaBuilder, integerExpression, integerExpression2) -> {
                                    // >= ym-3
                                    return criteriaBuilder.greaterThanOrEqualTo(integerExpression,
                                            criteriaBuilder.sum(integerExpression2, -3));
                                });
                    throw new IllegalArgumentException("未知的查询类型:" + type);
                });
            }
        };
    }

}
