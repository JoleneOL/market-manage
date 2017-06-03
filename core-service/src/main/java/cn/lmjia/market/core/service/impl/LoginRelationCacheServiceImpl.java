package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.cache.LoginRelation;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentSystem;
import cn.lmjia.market.core.repository.CustomerRepository;
import cn.lmjia.market.core.repository.cache.LoginRelationRepository;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.repository.deal.AgentSystemRepository;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.service.cache.LoginRelationCacheService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Service
public class LoginRelationCacheServiceImpl implements LoginRelationCacheService {

    private static final Log log = LogFactory.getLog(LoginRelationCacheServiceImpl.class);
    @Autowired
    private LoginRelationRepository loginRelationRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @Autowired
    private SystemService systemService;
    @Autowired
    private AgentSystemRepository agentSystemRepository;

    /**
     * 将新关系添加到关系库中去
     *
     * @param system              代理体系
     * @param relations           已存关系；新关系都应当跟已存关系矩形混合
     * @param loginRelationStream 新关系流
     */
    private void addExistingRelation(AgentSystem system, Set<LoginRelation> relations
            , Stream<LoginRelation> loginRelationStream) {
        relations.addAll(loginRelationStream
                .flatMap(relation -> {
                    // 根据当前的一个代理 扩展为一整套由已存在导入的关系
                    // 返回一个stream 表明 因为这个而新增的关系
                    List<LoginRelation> newR = new ArrayList<>();
                    newR.add(relation);
                    // 然后把to 是 我的 from的 也加进来
                    newR.addAll(relations.stream()
                            .filter(existingRelation -> existingRelation.getTo().equals(relation.getFrom()))
                            .map(existingRelation
                                    -> createRelationFromLevel(system, existingRelation.getFrom()
                                    , relation.getTo(), relation.getLevel()))
                            .collect(Collectors.toList()));
                    return newR.stream();
                }).collect(Collectors.toList()));
    }

    private LoginRelation createRelationFromLevel(AgentSystem system, Login from, Login to, int level) {
        LoginRelation relation = new LoginRelation();
        relation.setSystem(system);
        relation.setFrom(from);
        relation.setTo(to);
        relation.setLevel(level);
        return relation;
    }

    @Override
    public void rebuildAgentSystem(AgentSystem system) {
        loginRelationRepository.deleteBySystem(system);

        // 把所有上下线关系全部缓存起来！
        // 另外加上所有客户的
        List<AgentLevel> levels = agentLevelRepository.findBySystemAndSuperiorNotNull(system);
        Set<LoginRelation> relations = new HashSet<>();
        for (int i = 1; i < systemService.systemLevel(); i++) {
            int level = i;
            final Stream<LoginRelation> loginRelationStream = levels.stream()
                    // 一步处理一个等级 从高级到低级 因为低级关系依赖高级关系
                    .filter(agentLevel -> agentLevel.getLevel() == level)
                    // 先处理成一个原始关系
                    .map(agentLevel -> createRelationFromLevel(system, agentLevel.getSuperior().getLogin()
                            , agentLevel.getLogin(), agentLevel.getLevel()));

            addExistingRelation(system, relations, loginRelationStream);
        }

        // 然后是客户么
        Stream<LoginRelation> loginRelationStream = customerRepository.findByAgentLevel_SystemAndSuccessOrderTrue(system).stream()
                .map(customer
                        -> createRelationFromLevel(system, customer.getAgentLevel().getLogin(), customer.getLogin()
                        , Customer.LEVEL));
        addExistingRelation(system, relations, loginRelationStream);

        // 移除跟自己的关系
        saveValidRelations(relations);
    }

    private void saveValidRelations(Set<LoginRelation> relations) {
        loginRelationRepository.save(relations
                .stream()
                .filter(relation -> !relation.getFrom().equals(relation.getTo()))
                .collect(Collectors.toSet())
        );
    }

    @Override
    public void rebuildAll() {
        agentSystemRepository.findAll().forEach(this::rebuildAgentSystem);
    }

    @Override
    public void addCustomerCache(Customer customer) {
        // 只新增关系！
        final AgentSystem system = customer.getAgentLevel().getSystem();
        final Login from = customer.getAgentLevel().getLogin();
        Set<LoginRelation> relations = loginRelationRepository.findBySystemAndTo(system
                , from);

        final Login customerLogin = customer.getLogin();
        if (!loginRelationRepository.findBySystemAndFromAndToAndLevel(system, from, customerLogin, Customer.LEVEL).isEmpty()) {
            log.debug("客户" + customer + "的关系缓存已存在");
            return;
        }

        // 如果该关系已存在 则不重复添加！
        addExistingRelation(system, relations
                , Stream.of(createRelationFromLevel(system, from, customerLogin, Customer.LEVEL)));
        saveValidRelations(relations);
    }
}
