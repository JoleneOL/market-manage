package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.AgentLevelRepository;
import cn.lmjia.market.dealer.service.AgentService;
import me.jiangcai.lib.spring.data.AndSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.ArrayList;

/**
 * @author CJ
 */
@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Override
    public int agentLevel(AgentLevel level) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> integerCriteriaQuery = criteriaBuilder.createQuery(Integer.class);
        Root<AgentLevel> root = integerCriteriaQuery.from(AgentLevel.class);
//        integerCriteriaQuery = integerCriteriaQuery.select(agentLevelExpression(level.getId(), criteriaBuilder));
        integerCriteriaQuery = integerCriteriaQuery.select(agentLevelExpression(root, criteriaBuilder));
        integerCriteriaQuery = integerCriteriaQuery.where(criteriaBuilder.equal(root.get("id"), level.getId()));
        return entityManager.createQuery(integerCriteriaQuery).getSingleResult();
    }

    @Override
    public AgentLevel addAgent(Login login, String name, AgentLevel superior) {
        AgentLevel topLevel = null;

        AgentLevel current = superior;
        int count = systemLevel() - (current == null ? 0 : agentLevel(superior) + 1);// 几次 如果是最顶级的那么就是 systemLevel次
        if (count <= 0)
            throw new IllegalStateException("无法给" + superior + "添加下级代理商，违法了现在有的" + systemLevel() + "层架构");
        while (count-- > 0) {
            AgentLevel top = new AgentLevel();
            top.setLogin(login);
            top.setRank(name);
            top.setSuperior(current);
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
        // 先设置最高级别的
        return topLevel;
    }

    @Override
    public AgentLevel addTopAgent(Login login, String name) {
        return addAgent(login, name, null);
    }

    @Override
    public Specification<AgentLevel> manageable(Login login, String agentName) {
        final Specification<AgentLevel> nameSpecification;
        if (StringUtils.isEmpty(agentName)) {
            nameSpecification = null;
        } else {
            nameSpecification = (root, query, cb) -> {
                String name = "%" + agentName + "%";
//                Path<ContactWay> contactWayPath = cb.treat(root.get("login").get("contactWay"), ContactWay.class);
                Join<Login, AgentLevel> loginAgentLevelJoin = root.join("login");
                Join<ContactWay, Login> contactWayLoginJoin = loginAgentLevelJoin.join("contactWay", JoinType.LEFT);
//                Path<ContactWay> contactWayPath = root.get("login").get("contactWay");
                return cb.or(
                        cb.like(root.get("login").get("loginName"), name)
                        , cb.like(root.get("rank"), name)
                        , cb.like(contactWayLoginJoin.get("name"), name)
                        , cb.like(contactWayLoginJoin.get("mobile"), name)
                );
            };
        }
        if (login.isManageable())
            return (new AndSpecification<>((root, query, cb)
                    -> cb.isNull(root.get("superior")), nameSpecification));
        return (new AndSpecification<>(s(highestAgent(login)), nameSpecification));
    }

    @Override
    public Page<AgentLevel> manageable(Login login, String agentName, Pageable pageable) {
        return agentLevelRepository.findAll(manageable(login, agentName), pageable);
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

    private Specification<AgentLevel> s(AgentLevel agent) {
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
    public AgentLevel getAgent(long id) {
        return agentLevelRepository.getOne(id);
    }
}
