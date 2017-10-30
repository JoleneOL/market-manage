package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Login_;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentLevel_;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.repository.deal.AgentSystemRepository;
import cn.lmjia.market.core.service.AgentFinancingService;
import cn.lmjia.market.core.service.ContactWayService;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.service.cache.LoginRelationCacheService;
import cn.lmjia.market.dealer.service.AgentService;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.lib.spring.data.AndSpecification;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service("agentService")
public class AgentServiceImpl implements AgentService {

    private static final Log log = LogFactory.getLog(AgentServiceImpl.class);
    @Autowired
    private AgentFinancingService agentFinancingService;
    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SystemService systemService;
    @Autowired
    private AgentSystemRepository agentSystemRepository;
    @Autowired
    private LoginService loginService;
    @Autowired
    private LoginRelationCacheService loginRelationCacheService;
    @Autowired
    private ReadService readService;

    @Override
    public String getLoginTitle(int i) {
        return readService.getLoginTitle(i);
    }

    @Override
    public AgentLevel addAgent(Login who, Login login, String name, String levelTitle, LocalDate beginDate, LocalDate endDate
            , int firstPayment, int agencyFee, AgentLevel superior) {
        AgentSystem agentSystem;
        if (superior == null) {
            // TODO 顶级代理 可不是所有人可以添的
            // 顶级代理啊
            agentSystem = new AgentSystem();
            agentSystem = agentSystemRepository.save(agentSystem);
        } else
            agentSystem = superior.getSystem();
        AgentLevel newLevel = addAgent(who, login, name, levelTitle, beginDate, endDate, firstPayment, agencyFee, superior, agentSystem);
        loginRelationCacheService.rebuildAgentSystem(agentSystem);
        return newLevel;
    }

    @Override
    public Specification<MainOrder> manageableOrder(Login login) {
        if (login.isManageable())
            return null;
        // 目前只处理 代理商
        // 是购买者所可以代表的最低代理商 是否从属于 当前登录者所能代表的最高代理商
        final AgentLevel agentLevel = highestAgent(login);
        if (agentLevel == null) // 客户？
            return (root, query, cb) -> cb.equal(root.get(MainOrder_.orderBy), login);
        long id = agentLevel.getLogin().getId();
        return (root, query, cb)
                -> cb.equal(agentBelongsExpression(
                // 下单的人
                root.get(MainOrder_.orderBy).get(Login_.id).as(Integer.class)
//                root.get("customer").get("agentLevel").get("id").as(Integer.class)
                , cb.literal(id).as(Integer.class)
                , cb
        ), 1);
    }

    private AgentLevel addAgent(Login who, Login login, String name, String firstLevelTitle, LocalDate beginDate, LocalDate endDate
            , int firstPayment, int agencyFee, AgentLevel superior, AgentSystem system) {
        AgentLevel topLevel = null;

        AgentLevel current = superior;
        int count = systemService.systemLevel() - (current == null ? 0 : agentLevel(superior) + 1);
        // 几次 如果是最顶级的那么就是 systemLevel次
        if (count <= 0)
            throw new IllegalStateException("无法给" + superior + "添加下级代理商，违法了现在有的"
                    + systemService.systemLevel() + "层架构");

        // 设置货款
        final BigDecimal goodPayment = BigDecimal.valueOf(firstPayment);
        login.setCurrentGoodPayment(login.getCurrentGoodPayment().add(goodPayment));
        agentFinancingService.recordGoodPayment(login, goodPayment, null, null);


        final LocalDateTime now = LocalDateTime.now();
        while (count-- > 0) {
            AgentLevel top = new AgentLevel();
            top.setSystem(system);
            top.setCreatedBy(who);
            top.setCreatedTime(now);
            top.setLogin(login);
            top.setRank(name);
            top.setSuperior(current);
            if (current == null) {
                top.setLevel(0);
            } else {
                top.setLevel(current.getLevel() + 1);
            }
            top.setBeginDate(beginDate);
            top.setEndDate(endDate);
            final AgentLevel newAgentLevel = agentLevelRepository.save(top);
            if (current != null) {
                if (current.getSubAgents() == null)
                    current.setSubAgents(new ArrayList<>());
                current.getSubAgents().add(newAgentLevel);
                agentLevelRepository.save(current);
            }
            current = newAgentLevel;
            if (topLevel == null) {
                topLevel = current;
            }
        }
        // 设置代理费
        agentFinancingService.recordAgentFee(login, topLevel, BigDecimal.valueOf(agencyFee), null, null);
        if (!StringUtils.isEmpty(firstLevelTitle)) {
            topLevel.setLevelTitle(firstLevelTitle);
        }
        // 先设置最高级别的
        return topLevel;
    }

    @Override
    public Specification<AgentLevel> manageableAndRuling(boolean direct, Login login, String agentName) {
        return new AndSpecification<>(manageable(direct, login, agentName)
                , (root, query, cb)
                -> cb.lessThan(agentLevelExpression(root, cb), systemService.systemLevel() - 1));
    }

//    @SuppressWarnings("SpringJavaAutowiringInspection")
//    @Autowired
//    private EntityManager entityManager;
//
//    private Page<AgentInfo> agentInfo(Specification<AgentLevel> specification, Pageable pageable) {
//        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<AgentInfo> query = criteriaBuilder.createQuery(AgentInfo.class);
//        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
//        Root<AgentLevel> root = query.from(AgentLevel.class);
//        Root<AgentLevel> countRoot = countQuery.from(AgentLevel.class);
//
//        countQuery = countQuery.select(criteriaBuilder.count(countRoot));
//        countQuery = countQuery.where(specification.toPredicate(countRoot,countQuery,criteriaBuilder));
//        // 数据
//        Subquery<AgentLevel> query.subquery(AgentLevel.class);
//        query = query.select(criteriaBuilder.construct(
//                AgentInfo.class,
//                root,
//        ))
//
//
////        entityManager.find
//        return null;
//    }

    @Override
    public Specification<AgentLevel> manageable(boolean direct, Login login, String agentName) {
        final Specification<AgentLevel> nameSpecification;
        if (StringUtils.isEmpty(agentName)) {
            nameSpecification = null;
        } else {
            nameSpecification = (root, query, cb) -> {
                String name = "%" + agentName + "%";
//                Path<ContactWay> contactWayPath = cb.treat(root.get("login").get("contactWay"), ContactWay.class);
                Join<AgentLevel, Login> loginAgentLevelJoin = root.join(AgentLevel_.login);
                Join<ContactWay, Login> contactWayLoginJoin = loginAgentLevelJoin.join("contactWay", JoinType.LEFT);
//                Path<ContactWay> contactWayPath = root.get("login").get("contactWay");
                return cb.or(
                        cb.like(root.get(AgentLevel_.login).get("loginName"), name)
                        , cb.like(root.get("rank"), name)
                        , cb.like(contactWayLoginJoin.get("name"), name)
                        , cb.like(contactWayLoginJoin.get("mobile"), name)
                );
            };
        }
        if (login.isManageable())
            if (direct)
                return (new AndSpecification<>((root, query, cb)
                        -> cb.isNull(root.get("superior")), nameSpecification));
            else
                return nameSpecification;
        if (direct)
            return (new AndSpecification<>(directBelongsTo(highestAgent(login)), nameSpecification));
        else
            return (new AndSpecification<>(belongsTo(highestAgent(login)), nameSpecification));
    }

    @Override
    public Page<AgentLevel> manageable(Login login, String agentName, Pageable pageable) {
        return agentLevelRepository.findAll(manageable(true, login, agentName), pageable);
    }

    private Specification<AgentLevel> belongsTo(AgentLevel agent) {
        return (root, query, cb) -> cb.equal(agentBelongsExpression(root.get("id"), cb.literal(agent.getId()), cb), 1);
    }

    private Specification<AgentLevel> directBelongsTo(AgentLevel agent) {
        return (root, query, cb) -> cb.equal(root.get("superior"), agent);
    }

    @Override
    public AgentLevel highestAgent(Login login) {
        AgentLevel agentLevel = agentLevelRepository.findByLogin(login).stream()
                .findAny().orElse(null);
        if (agentLevel != null) {
            // 最高级别的
            AgentLevel current = agentLevel;
            while (current.getSuperior() != null && current.getSuperior().getLogin().equals(login)) {
                current = current.getSuperior();
            }
            return current;
        }
        return null;
    }

    @Override
    public AgentLevel getAgent(Login login, int level) {
        return agentLevelRepository.findTopByLoginAndLevel(login, level);
    }

    @Override
    public AgentLevel getAgent(long id) {
        return agentLevelRepository.getOne(id);
    }

    @Override
    public AgentSystem agentSystem(Login login) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AgentSystem> systemCriteriaQuery = criteriaBuilder.createQuery(AgentSystem.class);
        Root<AgentLevel> agentLevelRoot = systemCriteriaQuery.from(AgentLevel.class);
        systemCriteriaQuery = systemCriteriaQuery.select(agentLevelRoot.get("system"));
        systemCriteriaQuery = systemCriteriaQuery.where(criteriaBuilder.equal(agentLevelRoot.get(AgentLevel_.login), login));
        systemCriteriaQuery = systemCriteriaQuery.distinct(true);
        try {
            return entityManager.createQuery(systemCriteriaQuery).getSingleResult();
        } catch (NoResultException ex) {
            return agentSystem(login.getGuideUser());
//            systemCriteriaQuery = criteriaBuilder.createQuery(AgentSystem.class);
//            Root<Customer> customerRoot = systemCriteriaQuery.from(Customer.class);
//            systemCriteriaQuery = systemCriteriaQuery.select(customerRoot.get("agentLevel").get("system"));
//            systemCriteriaQuery = systemCriteriaQuery.where(criteriaBuilder.equal(customerRoot.get("login"), login));
//            return entityManager.createQuery(systemCriteriaQuery).getSingleResult();
        }
    }

    @Override
    public AgentLevel[] agentLine(Login login) {
        AgentLevel agentLevel = loginService.lowestAgentLevel(login);
        // 该进入查询了
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<AgentLevel> root = criteriaQuery.from(AgentLevel.class);
        // 按照level 查询 第一个是最长的……
        List<From<?, AgentLevel>> selectionList = new ArrayList<>(systemService.systemLevel());
        selectionList.add(root);
        int count = systemService.systemLevel() - 1;
        while (count-- > 0) {
            // 最后一个selection
            From<?, AgentLevel> last = selectionList.get(selectionList.size() - 1);
            selectionList.add(last.join("superior", JoinType.LEFT));
        }

        Collections.reverse(selectionList);
        criteriaQuery = criteriaQuery.multiselect(selectionList.stream().map(from -> from).collect(Collectors.toList()));
        criteriaQuery = criteriaQuery.where(criteriaBuilder.equal(root, agentLevel));

        Tuple tuple = entityManager.createQuery(criteriaQuery).getSingleResult();
        AgentLevel[] levels = new AgentLevel[systemService.systemLevel()];
        for (int i = 0; i < levels.length; i++) {
            levels[i] = tuple.get(i, AgentLevel.class);
        }
        return levels;
    }

//    @Override
//    public AgentLevel[] recommendAgentLine(Login login) {
//        // 获得一个完整的推荐链，第一个必然是 level 0 然后是 1,2,3,etc...
//        // 最多执行n次查询
//        AgentLevel current = highestAgent(login);
//        //
//        int levelGap = current.getLevel();
//
//        // 自己并不在推荐列表中！
//        AgentLevel[] result = new AgentLevel[systemService.systemLevel()];
//        while (levelGap-- > 0) {
//            result[levelGap] = agentLevelRepository.findByLevelAndLoginAndSystem(levelGap, current.getLogin().getGuideUser(), current.getSystem());
//            current = result[levelGap];
//        }
//
//        return result;
//    }

    @Override
    public AgentLevel[] recommendAgentLine(Login login) {
        // 获得一个完整的推荐链，第一个必然是 level 0 然后是 1,2,3,etc...
        // 最多执行n次查询
        // 获取 平行推荐，然后上一级 再找平行推荐
        AgentLevel[] line = agentLine(login);
        AgentLevel[] result = new AgentLevel[systemService.systemLevel()];
//        AgentLevel current = loginService.lowestAgentLevel(login);
//        if (current == null) {
//            // 如果login是一个客户，那么应该采用它引导者
//            return recommendAgentLine(login.getGuideUser());
//        }
        for (int i = 0; i < line.length; i++) {
            result[i] = newParallelRecommend(line[i].getLogin().getGuideUser(), i);
        }
//        int count = systemService.systemLevel();
////        result[count] = current;
//        while (count-- > 0) {
//            // 下一个平推者
//            result[count] = newParallelRecommend(login.getGuideUser(), count);
//        }
        return result;
    }

    /**
     * 某人推荐了它，并且某人拥有一个比它高1的代理商
     * 如果没有，寻找它推荐人 是否拥有一个
     *
     * @param guideUser 某人的第一个选择
     * @param level
     * @return 获取agent的特定等级的平级推荐代理商
     */
    private AgentLevel newParallelRecommend(Login guideUser, int level) {
        if (guideUser == null)
            return agentLevelRepository.findTopByLevelOrderById(level);
        AgentLevel level1 = agentLevelRepository.findTopByLevelAndLogin(level, guideUser);
        if (level1 == null)
            return newParallelRecommend(guideUser.getGuideUser(), level);
        return level1;
    }


    @Override
    public AgentLevel addressLevel(Address address) {
        // 地址等同于 代理商
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AgentLevel> query = criteriaBuilder.createQuery(AgentLevel.class);
        Root<AgentLevel> root = query.from(AgentLevel.class);
        query = query.where(
                criteriaBuilder.and(
                        Address.AlmostMatch(
                                ContactWayService.addressForLogin(root.join(AgentLevel_.login), criteriaBuilder)
                                , address
                                , criteriaBuilder)
                        , criteriaBuilder.equal(root.get("level").as(Integer.class), systemService.addressRateForLevel())
                        , criteriaBuilder.isNull(root.get("levelTitle"))
                ));
        List<AgentLevel> agentLevels = entityManager.createQuery(query).getResultList();
        // 如何择优？ 它没有更高级别的了！
        return agentLevels.stream()
                .filter(agentLevel
                        -> !agentLevel.getSuperior().getLogin().equals(agentLevel.getLogin()))
                .peek(agentLevel
                        -> log.info("过滤之后找到" + agentLevel))
                .findFirst()
                .orElse(agentLevels.isEmpty() ? null : agentLevels.get(0));
    }

    @Override
    public void healthCheck(AgentSystem system) {
        //  = null 的 必然是0 依次类推
        Set<AgentLevel> superiors = null;
        int level = 0;
        while (true) {
            final List<AgentLevel> list;
            if (superiors == null)
                list = agentLevelRepository.findBySuperiorAndSystem(null, system);
            else {
                list = superiors.stream()
                        .map(superior -> agentLevelRepository.findBySuperiorAndSystem(superior, system))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
            }

            if (list.isEmpty()) {
                log.info(system + "健康检查完成");
                return;
            }

            // level 都是等于 level
            int targetLevel = level;
            list.stream()
                    .filter(agentLevel -> agentLevel.getLevel() != targetLevel)
                    .findFirst()
                    .ifPresent(agentLevel -> {
                        throw new IllegalStateException(agentLevel + "等级不正确，应该是：" + targetLevel);
                    });

            superiors = list.stream()
                    .collect(Collectors.toSet());
            level++;
        }
    }

    @Override
    public boolean isAgentLogin(String loginName) {
        Login login = loginService.byLoginName(loginName);
        return login != null && highestAgent(login) != null;
    }

}
