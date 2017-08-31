package cn.lmjia.market.dealer.controller.team;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Login_;
import cn.lmjia.market.core.entity.cache.LoginRelation;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentLevel_;
import cn.lmjia.market.core.model.ApiResult;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.IndefiniteFieldDefinition;
import cn.lmjia.market.core.row.IndefiniteRowDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.IndefiniteFieldBuilder;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Controller
public class TeamDataController {

    public static final DateTimeFormatter teamDateFormatter = DateTimeFormatter.ofPattern("yy-M-d", Locale.CHINA);
    @Autowired
    private SystemService systemService;
    @Autowired
    private AgentService agentService;
    //    @Autowired
    private ConversionService conversionService;
    @Autowired
    private ApplicationContext applicationContext;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ReadService readService;

    public static String mosaicMobile(String mobile) {
        if (StringUtils.isEmpty(mobile))
            return "***";
        if (mobile.length() <= 7)
            return "***";
        char[] c1 = mobile.substring(0, 3).toCharArray();
        char[] c2 = mobile.substring(mobile.length() - 4, mobile.length()).toCharArray();
        char[] m = new char[mobile.length() - c1.length - c2.length];
        for (int i = 0; i < m.length; i++) {
            Array.setChar(m, i, '*');
        }
        char[] all = new char[c1.length + m.length + c2.length];
        System.arraycopy(c1, 0, all, 0, c1.length);
        System.arraycopy(m, 0, all, c1.length, m.length);
        System.arraycopy(c2, 0, all, c1.length + m.length, c2.length);
        return new String(all);
    }

    @GetMapping("/api/subordinate")
    @Transactional(readOnly = true)
    @ResponseBody
    public ApiResult subordinate(int page, @AuthenticationPrincipal Login login) {
        // 如果我是代理商 渲染旗下代理商，否者渲染推荐的爱心天使
        AgentLevel agentLevel = agentService.highestAgent(login);
        if (agentLevel == null) {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<Login> root = cq.from(Login.class);
            return ApiResult.withOk(
                    entityManager.createQuery(
                            cq
                                    .multiselect(
                                            root.get(Login_.id)
                                            , ReadService.nameForLogin(root, cb)
                                            , ReadService.agentLevelForLogin(root, cb)
                                            , root.get(Login_.createdTime)
                                            , ReadService.mobileForLogin(root, cb)
                                            , cb.literal(null)
                                    )
                                    .where(
                                            cb.equal(root.get(Login_.guideUser), login)
                                            , cb.isTrue(root.get(Login_.successOrder))
                                    )
                                    .orderBy(cb.desc(root.get(Login_.createdTime)))
                    ).setMaxResults(20)
                            .setFirstResult(page * 20)
                            .getResultList()
                            .stream()
                            .map(this::member)
                            .collect(Collectors.toList())
            );
        }
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<AgentLevel> root = cq.from(AgentLevel.class);

        return ApiResult.withOk(
                entityManager.createQuery(
                        cq.multiselect(
                                root.get(AgentLevel_.id)
                                , ReadService.nameForLogin(root.join(AgentLevel_.login), cb)
                                , root.get(AgentLevel_.level)
                                , root.get(AgentLevel_.createdTime)
                                , ReadService.mobileForLogin(root.join(AgentLevel_.login), cb)
                                , cb.sum(root.get(AgentLevel_.level), 1)
                        ).where(
                                cb.equal(root.get(AgentLevel_.superior), agentLevel)
                        )
                                .orderBy(cb.desc(root.get(AgentLevel_.createdTime)))
                ).setMaxResults(20)
                        .setFirstResult(page * 20)
                        .getResultList()
                        .stream()
                        .map(this::member)
                        .collect(Collectors.toList())
        );

    }

    private Object member(Tuple tuple) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", tuple.get(0));
        data.put("name", tuple.get(1));
        data.put("rank", readService.getLoginTitle(tuple.get(2, Integer.class)));
        data.put("joinTime", tuple.get(3, LocalDateTime.class).format(teamDateFormatter));
        data.put("phone", mosaicMobile(tuple.get(4, String.class)));
        Object object = tuple.get(5);
        if (object != null)
            data.put("nextRank", object);
        return data;
    }


    // all 全部
    // 总 1 所谓省总代
    // 分 2 总代理 移除
    // 经销 3 经销商
    // 客户 4 100 爱心天使
    // 非正式用户 5
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
    public IndefiniteRowDefinition teamList(@AuthenticationPrincipal Login login, String rank) {
        if (conversionService == null)
            conversionService = applicationContext.getBean(ConversionService.class);

        Integer level = fromRank(rank);
        return getTeamRowDefinition(login, level);
    }

    private IndefiniteRowDefinition getTeamRowDefinition(final Login login, final Integer level) {
        // 推荐的人哦
        return new IndefiniteRowDefinition() {

            @Override
            public List<IndefiniteFieldDefinition> fields() {
                return Arrays.asList(
                        IndefiniteFieldBuilder.asName("name").build()
                        , IndefiniteFieldBuilder.asName("rank")
                                .addFormat((o, mediaType) -> agentService.getLoginTitle(((Number) o).intValue()))
                                .build()
                        , IndefiniteFieldBuilder.asName("joinTime")
                                .addFormat((o, mediaType) -> conversionService.convert(o, String.class))
                                .build()
                        , IndefiniteFieldBuilder.asName("phone").build()
                );
            }

            @Override
            public Query createQuery(EntityManager entityManager) {
                // 临时计划 一般情况下 再看不到爱心天使 只有选择了爱心天使才可以看到爱心天使
                if (level != null && level == Customer.LEVEL) {
                    // 只展示爱心天使
                    return entityManager.createQuery(
                            "select " +
                                    "function('IFNULL',cw.name,l.loginName) " +
                                    ",100 " +
                                    ", l.createdTime" +
                                    ",function('IFNULL',cw.mobile,l.loginName) " +
                                    "from Login as l " +
                                    "left join l.contactWay as cw " +
                                    "where l.guideUser=:current and l.successOrder=true"
                    )
                            .setParameter("current", login);
                }
                Query query = entityManager.createQuery("select " +
                        "function('IFNULL',cw.name,relation.to.loginName) " +
                        ",min(relation.level) " +
                        ",relation.to.createdTime " +
                        ",function('IFNULL',cw.mobile,relation.to.loginName)" +
                        "from LoginRelation as relation " +
                        "left join relation.to.contactWay as cw " +
                        "where relation.to in (select l from Login as l where  l.guideUser=:current) " +
                        "group by relation.to " +
                        (level == null ? "" : " having min(relation.level)=:level " +
                                ""))
                        .setParameter("current", login);
                if (level == null)
                    return query;
                return query.setParameter("level", level);
            }
        };
    }


    @RowCustom(dramatizer = ApiDramatizer.class, distinct = false)
    @GetMapping("/api/teamList2")
    public RowDefinition<LoginRelation> teamList2(@AuthenticationPrincipal Login login, String rank) {
        if (conversionService == null)
            conversionService = applicationContext.getBean(ConversionService.class);
        Integer level = fromRank(rank);
        return new LoginRelationRows(level) {

            @Override
            protected Specification<LoginRelation> newSpecification() {
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

        protected abstract Specification<LoginRelation> newSpecification();

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
