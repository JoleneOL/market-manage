package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.AgentLevelRepository;
import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author CJ
 */
@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentLevelRepository agentLevelRepository;

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
    public Page<AgentLevel> manageable(Login login, Pageable pageable) {
        if (login.isManageable())
            return agentLevelRepository.findAll((root, query, cb) -> cb.isNull(root.get("superior")), pageable);
        return agentLevelRepository.findAll(s(highestAgent(login)), pageable);
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
}
