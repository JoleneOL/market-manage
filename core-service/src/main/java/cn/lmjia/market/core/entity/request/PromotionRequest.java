package cn.lmjia.market.core.entity.request;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.support.PaymentStatus;
import cn.lmjia.market.core.entity.support.PromotionRequestStatus;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.service.ReadService;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 提升申请
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class PromotionRequest implements PayableOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 谁的申请
     */
    @ManyToOne
    private Login whose;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime requestTime;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime changeTime;
    @ManyToOne
    private Manager changer;
    private PromotionRequestStatus requestStatus;
    private PaymentStatus paymentStatus;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime PayTime;

    /**
     * 成功支付的支付订单
     */
    @ManyToOne
    private PayOrder payOrder;
    /**
     * 需支付金额 可为null
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal price;
    /**
     * 公司名称
     */
    private String name;
    private Address address;
    /**
     * 类型,不知道以后还有什么 就用int了
     * 1: 经销商
     * 2: 代理商
     * 3: 省代理（其实是区代理）
     */
    private int type;
    @Column(length = 60)
    private String frontImagePath;
    @Column(length = 60)
    private String backImagePath;
    @Column(length = 68)
    private String businessLicensePath;

    public static RowDefinition<PromotionRequest> Rows(String applicationDate, String mobile, ReadService readService
            , ResourceService resourceService, ConversionService conversionService, Function<Integer, String> toLevelName) {
        return new RowDefinition<PromotionRequest>() {
            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<PromotionRequest> root) {
                return Collections.singletonList(criteriaBuilder.desc(root.get("requestTime")));
            }

            @Override
            public Class<PromotionRequest> entityClass() {
                return PromotionRequest.class;
            }

            @Override
            public List<FieldDefinition<PromotionRequest>> fields() {
                return Arrays.asList(
                        FieldBuilder.asName(PromotionRequest.class, "id")
                                .build()
                        , FieldBuilder.asName(PromotionRequest.class, "name")
                                .addBiSelect((promotionRequestRoot, criteriaBuilder) -> ReadService.nameForLogin(promotionRequestRoot.join("whose"), criteriaBuilder))
                                .withoutOrder()
                                .build()
                        , FieldBuilder.asName(PromotionRequest.class, "currentLevel")
                                .addBiSelect((promotionRequestRoot, criteriaBuilder) -> ReadService.agentLevelForLogin(promotionRequestRoot.join("whose"), criteriaBuilder))
                                .addFormat((object, type) -> readService.getLoginTitle((int) object))
                                .build()
                        , FieldBuilder.asName(PromotionRequest.class, "applicationLevel")
                                .addSelect(promotionRequestRoot -> promotionRequestRoot.get("type"))
                                .addFormat((object, type) -> toLevelName.apply((int) object))
                                .build()
//                        , FieldBuilder.asName(PromotionRequest.class, "type")
//                                .build()
                        , FieldBuilder.asName(PromotionRequest.class, "address")
                                .addFormat((object, type) -> object.toString())
                                .build()
                        , FieldBuilder.asName(PromotionRequest.class, "mobile")
                                .addBiSelect((promotionRequestRoot, criteriaBuilder) -> ReadService.mobileForLogin(promotionRequestRoot.join("whose"), criteriaBuilder))
                                .build()
                        , resourceUrl(resourceService, "cardFront", "frontImagePath")
                        , resourceUrl(resourceService, "cardBack", "backImagePath")
                        , resourceUrl(resourceService, "businessLicense", "businessLicensePath")
                        , FieldBuilder.asName(PromotionRequest.class, "paymentStatus")
                                .addFormat((object, type) -> conversionService.convert(object, String.class))
                                .build()
                        , FieldBuilder.asName(PromotionRequest.class, "applicationDate")
                                .addSelect(promotionRequestRoot -> promotionRequestRoot.get("requestTime"))
                                .addFormat((object, type) -> conversionService.convert(object, String.class))
                                .build()
                        , FieldBuilder.asName(PromotionRequest.class, "operator")
                                .addBiSelect(((promotionRequestRoot, criteriaBuilder) ->
                                        ReadService.nameForLogin(promotionRequestRoot.join("changer", JoinType.LEFT), criteriaBuilder)
                                ))
                                .build()
                        , FieldBuilder.asName(PromotionRequest.class, "status")
                                .addSelect(promotionRequestRoot -> promotionRequestRoot.get("requestStatus"))
                                .addFormat((object, type) -> {
                                    PromotionRequestStatus status = (PromotionRequestStatus) object;
                                    switch (status) {
                                        case rejected:
                                            return "已拒绝";
                                        case approved:
                                            return "已批准";
                                        default:
                                            return "未处理";
                                    }
                                })
                                .build()
                        , FieldBuilder.asName(PromotionRequest.class, "stateCode")
                                .addSelect(promotionRequestRoot -> promotionRequestRoot.get("requestStatus"))
                                .addFormat((object, type) -> {
                                    PromotionRequestStatus status = (PromotionRequestStatus) object;
                                    switch (status) {
                                        case rejected:
                                            return 2;
                                        case approved:
                                            return 1;
                                        default:
                                            return 0;
                                    }
                                })
                                .build()
                );
            }

            private FieldDefinition<PromotionRequest> resourceUrl(ResourceService resourceService, String name, String fieldName) {
                return FieldBuilder.asName(PromotionRequest.class, name)
                        .addSelect(promotionRequestRoot -> promotionRequestRoot.get(fieldName))
                        .addFormat((object, type) -> {
                            if (StringUtils.isEmpty(object))
                                return null;
                            try {
                                return resourceService.getResource(object.toString()).httpUrl().toString();
                            } catch (IOException e) {
                                throw new InternalError("no", e);
                            }
                        })
                        .build();
            }

            @Override
            public Specification<PromotionRequest> specification() {
                return (root, query, cb) -> {
                    Predicate predicate = cb.equal(root.get("requestStatus"), PromotionRequestStatus.requested);
                    if (!StringUtils.isEmpty(mobile)) {
                        predicate = cb.and(predicate
                                , cb.like(ReadService.mobileForLogin(root.join("whose"), cb), "%" + mobile + "%")
                        );
                    }
                    if (!StringUtils.isEmpty(applicationDate)) {
                        if (applicationDate.equalsIgnoreCase("month"))
                            predicate = cb.and(predicate
                                    , JpaFunctionUtils.YearAndMonthEqual(cb, root.get("requestTime")
                                            , LocalDate.now()));
                        else if (applicationDate.equalsIgnoreCase("quarter"))
                            predicate = cb.and(predicate
                                    , JpaFunctionUtils.YM(cb, root.get("requestTime")
                                            , LocalDate.now()
                                            , (criteriaBuilder, integerExpression, integerExpression2) -> {
                                                // >= ym-3
                                                return criteriaBuilder.greaterThanOrEqualTo(integerExpression,
                                                        criteriaBuilder.sum(integerExpression2, -3));
                                            }));
                        else
                            predicate = cb.and(predicate
                                    , JpaFunctionUtils.YM(cb, root.get("requestTime")
                                            , LocalDate.now()
                                            , (criteriaBuilder, integerExpression, integerExpression2) -> {
                                                // >= ym-3
                                                return criteriaBuilder.greaterThanOrEqualTo(integerExpression,
                                                        criteriaBuilder.sum(integerExpression2, -12));
                                            }));
                    }
                    return predicate;
                };
            }
        };
    }

    @Override
    public Serializable getPayableOrderId() {
        return "PromotionRequest-" + getId();
    }

    @Override
    public BigDecimal getOrderDueAmount() {
        if (type == 1)
            return price;
        return null;
    }

    @Override
    public String getOrderProductName() {
        return "经销商开通费";
    }

    @Override
    public String getOrderBody() {
        return "经销商开通费";
    }
}
