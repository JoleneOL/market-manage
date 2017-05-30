package cn.lmjia.market.dealer.controller.team;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.cache.LoginRelation;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.util.ApiDramatizer;
import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;

/**
 * @author CJ
 */
@Controller
public class TeamDataController {

    @Autowired
    private SystemService systemService;
    @Autowired
    private AgentService agentService;
    //    @Autowired
    private ConversionService conversionService;
    @Autowired
    private ApplicationContext applicationContext;

    // all
    // 总 1
    // 分 2
    // 经销 3
    // 客户 4 100
    private Integer fromRank(String rank) {
        // 系统的最底层 我们认为是3
        if (StringUtils.isEmpty(rank))
            return null;
        if (rank.equalsIgnoreCase("all"))
            return null;
        int x = NumberUtils.parseNumber(rank, Integer.class);
        if (x == 4)
            return Customer.LEVEL;
        return systemService.systemLevel() - 1 + x - 3;
    }


    @RowCustom(dramatizer = ApiDramatizer.class, distinct = false)
    @GetMapping("/api/teamList")
    public RowDefinition<LoginRelation> teamList(@AuthenticationPrincipal Login login, String rank) {
        if (conversionService == null)
            conversionService = applicationContext.getBean(ConversionService.class);
        Integer level = fromRank(rank);
        return new RowDefinition<LoginRelation>() {
            @Override
            public Class<LoginRelation> entityClass() {
                return LoginRelation.class;
            }

//            @Override
//            public Expression<?> count(CriteriaQuery<Long> countQuery, CriteriaBuilder criteriaBuilder, Root<LoginRelation> root) {
//                Subquery<Long> sub = countQuery.subquery(Long.class);
//                Root<LoginRelation> relationRoot = sub.from(LoginRelation.class);
//                sub = sub.where(newSpecification(relationRoot, sub, criteriaBuilder));
//                sub = sub.select(relationRoot.get("to"));
//                sub = sub.distinct(true);
//
//                return criteriaBuilder.count(sub);
//            }

            @Override
            public List<FieldDefinition<LoginRelation>> fields() {
                return Arrays.asList(
                        FieldBuilder.asName(LoginRelation.class, "name")
                                .addBiSelect((loginRelationRoot, criteriaBuilder)
                                        -> ReadService.nameForLogin(loginRelationRoot.join("to"), criteriaBuilder))
                                .build()
                        , FieldBuilder.asName(LoginRelation.class, "rank")
                                .addBiSelect((loginRelationRoot, criteriaBuilder)
                                        -> criteriaBuilder.min(loginRelationRoot.get("level")))
                                .addFormat((o, mediaType) -> agentService.getLoginTitle((Integer) o))
                                .build()
                        , FieldBuilder.asName(LoginRelation.class, "joinTime")
                                .addSelect(loginRelationRoot -> loginRelationRoot.get("to").get("createdTime"))
                                .addFormat((o, mediaType) -> conversionService.convert(o, String.class))
                                .build()
                        , FieldBuilder.asName(LoginRelation.class, "phone")
                                .addBiSelect((loginRelationRoot, criteriaBuilder)
                                        -> ReadService.mobileForLogin(loginRelationRoot.join("to"), criteriaBuilder))
                                .build()
                );
            }

            private Predicate newSpecification(Root<LoginRelation> root, AbstractQuery<?> query, CriteriaBuilder cb) {
//                if (!(query instanceof Subquery) && query.getResultType() == Long.class) {
//                    return cb.isTrue(cb.literal(true));
//                }
                query.groupBy(root.get("to"));
                Predicate predicate = cb.equal(root.get("from"), login);

//                    if (level == null)
//                        query.having(predicate);
//                    else
//                        query.having(predicate
//                                , cb.equal(root.get("level"), level));
//
//                    return cb.isTrue(cb.literal(true));
                if (level == null)
                    return predicate;
                return cb.and(predicate
                        , cb.equal(root.get("level"), level));
            }

            @Override
            public Specification<LoginRelation> specification() {
                return this::newSpecification;
            }
        };
    }


}
