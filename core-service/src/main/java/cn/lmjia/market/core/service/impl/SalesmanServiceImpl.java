package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.entity.deal.Commission_;
import cn.lmjia.market.core.entity.deal.OrderCommission_;
import cn.lmjia.market.core.entity.deal.SalesAchievement;
import cn.lmjia.market.core.entity.deal.SalesAchievement_;
import cn.lmjia.market.core.entity.deal.Salesman;
import cn.lmjia.market.core.entity.support.CommissionType;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.repository.deal.SalesAchievementRepository;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SalesmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author CJ
 */
@Service
public class SalesmanServiceImpl implements SalesmanService {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SalesAchievementRepository salesAchievementRepository;
    @Autowired
    private ConversionService conversionService;

    @Override
    public void salesmanShareTo(long salesmanId, Login login) {
        SalesAchievement achievement = new SalesAchievement();
        achievement.setTargetLogin(login);
        achievement.setTargetTime(LocalDateTime.now());
        achievement.setWhose(get(salesmanId));
        salesAchievementRepository.save(achievement);
    }

    @Override
    public SalesAchievement pick(Login login) {
        SalesAchievement achievement = salesAchievementRepository
                .findTop1ByTargetLoginAndPickedFalseAndWhose_EnableTrueOrderByTargetTimeDesc(login);
        if (achievement == null) {
            return null;
        }
        // 把之前未pick的记录 清理掉 因为没有任何意义了
        salesAchievementRepository.findByTargetLoginAndPickedFalse(login).stream()
                .filter(salesAchievement -> !salesAchievement.equals(achievement))
                .forEach(salesAchievementRepository::delete);
        achievement.setPicked(true);
        return achievement;
    }

    @Override
    public List<SalesAchievement> all(Salesman salesman) {
        return salesAchievementRepository.findByWhose(salesman);
    }

    @Override
    public Salesman get(long id) {
        return entityManager.getReference(Salesman.class, id);
//        return salesmanRepository.getOne(id);
//        return null;
    }

    @Override
    public Salesman newSalesman(Login login, BigDecimal rate, String rank) {
        Salesman salesman = new Salesman();
        salesman.setCreatedTime(LocalDateTime.now());
        salesman.setEnable(true);
        salesman.setLogin(login);
        salesman.setSalesRate(rate);
        salesman.setRank(rank);
        entityManager.persist(salesman);
        return salesman;
    }

    @Override
    public SalesAchievement getAchievement(long id) {
        return salesAchievementRepository.getOne(id);
    }

    @Override
    public RowDefinition<SalesAchievement> data(Login login, LocalDate date, Boolean remark, Boolean deal) {
        return new RowDefinition<SalesAchievement>() {
            private Join<SalesAchievement, MainOrder> orderPath;
            private Join<SalesAchievement, Login> loginPath;

            @Override
            public Class<SalesAchievement> entityClass() {
                return SalesAchievement.class;
            }

            @Override
            public List<FieldDefinition<SalesAchievement>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(SalesAchievement.class, "name")
                                .addBiSelect((salesAchievementRoot, criteriaBuilder)
                                        -> {
                                    loginPath = salesAchievementRoot.join(SalesAchievement_.targetLogin);
                                    return ReadService
                                            .nameForLogin(loginPath, criteriaBuilder);
                                })
                                .build()
                        , FieldBuilder.asName(SalesAchievement.class, "time")
                                .addSelect(salesAchievementRoot -> salesAchievementRoot.get(SalesAchievement_.targetTime))
                                .addFormat((data, type) -> conversionService.convert(data, String.class))
                                .build()
                        , FieldBuilder.asName(SalesAchievement.class, "tel")
                                .addBiSelect((salesAchievementRoot, criteriaBuilder)
                                        -> ReadService.mobileForLogin(loginPath, criteriaBuilder))
                                .build()
                        , FieldBuilder.asName(SalesAchievement.class, "statusCode")
                                .addBiSelect((salesAchievementRoot, criteriaBuilder) -> {
                                    orderPath = salesAchievementRoot.join(SalesAchievement_.mainOrder, JoinType.LEFT);
                                    return criteriaBuilder.or(orderPath.isNull(), MainOrder.getOrderPaySuccess(orderPath, criteriaBuilder).not());
                                })
                                .addFormat((data, type) -> {
                                    boolean success = !(boolean) data;
                                    return success ? 1 : 0;
                                })
                                .build()
                        , FieldBuilder.asName(SalesAchievement.class, "sum")
                                .addSelect(salesAchievementRoot -> orderPath.get(MainOrder_.goodCommissioningPriceAmountIndependent))
                                .build()
                        // 佣金  这个就很难了……
                        // 应该用子查询的方式
                        , FieldBuilder.asName(SalesAchievement.class, "comm")
                                .addOwnSelect((root, cb, query) -> {
                                    Subquery<BigDecimal> comm = query.subquery(BigDecimal.class);
                                    Root<Commission> root1 = comm.from(Commission.class);
                                    return comm
                                            .select(cb.sum(root1.get(Commission_.amount)))
                                            .where(cb.equal(root1.get(Commission_.type), CommissionType.sales)
                                                    , cb.equal(root1.get(Commission_.orderCommission).get(OrderCommission_.source), orderPath)
                                                    , cb.equal(root1.get(Commission_.who), login)
                                            )
                                            .groupBy(root1.get(Commission_.orderCommission))
                                            ;
                                })
                                .build()
                );
            }

            @Override
            public Specification<SalesAchievement> specification() {
                return (root, query, cb) -> {
                    Predicate predicate = cb.isTrue(root.get(SalesAchievement_.picked));
                    if (date != null) {
                        predicate = cb.and(predicate, JpaFunctionUtils.dateEqual(cb
                                , root.get(SalesAchievement_.targetTime), date));
                    }
                    if (remark != null) {
                        final Path<String> remarkPath = root.get(SalesAchievement_.remark);
                        predicate = cb.and(predicate, remark ? remarkPath.isNull() : remarkPath.isNotNull());
                    }
                    if (deal != null) {
                        Predicate next;
                        if (deal)
                            next = cb.and(orderPath.isNotNull(), MainOrder.getOrderPaySuccess(orderPath, cb));
                        else
                            next = cb.or(orderPath.isNull(), MainOrder.getOrderPaySuccess(orderPath, cb).not());
                        predicate = cb.and(predicate, next);
                    }
                    return predicate;
                };
            }
        };
    }
}
