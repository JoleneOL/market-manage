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
import me.jiangcai.lib.spring.data.AndSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

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
    @GetMapping("/api/teamList2")
    public RowDefinition<LoginRelation> teamList(@AuthenticationPrincipal Login login, String rank) {
        if (conversionService == null)
            conversionService = applicationContext.getBean(ConversionService.class);
        Integer level = fromRank(rank);
        // 推荐的人哦
        return new LoginRelationRows(level) {

            @Override
            Specification<LoginRelation> newSpecification() {
                return null;
            }
        };
    }


    @RowCustom(dramatizer = ApiDramatizer.class, distinct = false)
    @GetMapping("/api/teamList")
    public RowDefinition<LoginRelation> teamList2(@AuthenticationPrincipal Login login, String rank) {
        if (conversionService == null)
            conversionService = applicationContext.getBean(ConversionService.class);
        Integer level = fromRank(rank);
        return new LoginRelationRows(level) {

            @Override
            Specification<LoginRelation> newSpecification() {
                return (root, query, cb) -> {
                    query.groupBy(root.get("to"));
                    return cb.equal(root.get("from"), login);
                };
            }
        };
    }


    private abstract class LoginRelationRows implements RowDefinition<LoginRelation> {

        private final Integer level;

        protected LoginRelationRows(Integer level) {
            this.level = level;
        }

        @Override
        public Class<LoginRelation> entityClass() {
            return LoginRelation.class;
        }

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

        private Specification<LoginRelation> fixedSpecification() {
            if (level == null)
                return null;
            return (root, query, cb) -> cb.equal(root.get("level"), level);
        }

        @Override
        public final Specification<LoginRelation> specification() {
            return new AndSpecification<>(fixedSpecification(), newSpecification());
        }

        abstract Specification<LoginRelation> newSpecification();

        //        private Predicate newSpecification(Root<LoginRelation> root, AbstractQuery<?> query, CriteriaBuilder cb) {
////                if (!(query instanceof Subquery) && query.getResultType() == Long.class) {
////                    return cb.isTrue(cb.literal(true));
////                }
//            query.groupBy(root.get("to"));
//            Predicate predicate = cb.equal(root.get("from"), login);
//
////                    if (level == null)
////                        query.having(predicate);
////                    else
////                        query.having(predicate
////                                , cb.equal(root.get("level"), level));
////
////                    return cb.isTrue(cb.literal(true));
//            if (level == null)
//                return predicate;
//            return cb.and(predicate
//                    , cb.equal(root.get("level"), level));
//        }
//
//        @Override
//        public Specification<LoginRelation> specification() {
//            return this::newSpecification;
//        }
    }
}
