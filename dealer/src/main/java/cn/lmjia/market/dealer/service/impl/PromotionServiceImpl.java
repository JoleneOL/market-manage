package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentLevel_;
import cn.lmjia.market.core.event.LoginRelationChangedEvent;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.service.cache.LoginRelationCacheService;
import cn.lmjia.market.dealer.service.AgentService;
import cn.lmjia.market.dealer.service.PromotionService;
import cn.lmjia.market.dealer.service.TeamService;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * @author CJ
 */
@Service
public class PromotionServiceImpl implements PromotionService {

    private static final Log log = LogFactory.getLog(PromotionServiceImpl.class);
    @Autowired
    private LoginRelationCacheService loginRelationCacheService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @Autowired
    private ReadService readService;
    @Autowired
    private LoginService loginService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    private boolean inited;

    @PostConstruct
    @Transactional
    public void init() {
        realInit();
    }

    private void realInit() {
        // 自动升级3 为  2
        if (inited)
            return;
        inited = true;
        log.info("PromotionServiceImpl的realInit方法执行了!!!");
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Login> cq = cb.createQuery(Login.class);
        Root<Login> root = cq.from(Login.class);

        entityManager.createQuery(
                cq.where(cb.equal(ReadService.agentLevelForLogin(root, cb), 3))
        )
                .getResultList()
                .forEach(login -> {
                    log.info("212更新自动升级代理商 " + login.getId() + ":" + readService.nameForPrincipal(login));
                    AgentLevel top = agentService.highestAgent(login);
                    agentLevelUpgrade(login, top);
                });

        // 修复一个数据BUG
        // 若自己存在更高(小)级别的代理 那么必然是从属于它
        CriteriaQuery<AgentLevel> badLevelCQ = cb.createQuery(AgentLevel.class);
        Root<AgentLevel> badLevelRoot = badLevelCQ.from(AgentLevel.class);
        // 它的上级
        Join<AgentLevel, AgentLevel> superior = badLevelRoot.join(AgentLevel_.superior);
        // 它的同用户的更高级别
        Join<AgentLevel, Login> login = badLevelRoot.join(AgentLevel_.login);
        Subquery<AgentLevel> highAgent = badLevelCQ.subquery(AgentLevel.class);
        Root<AgentLevel> highAgentRoot = highAgent.from(AgentLevel.class);
        highAgent = highAgent
                .where(cb.equal(highAgentRoot.get(AgentLevel_.login), login)
                        , cb.equal(highAgentRoot.get(AgentLevel_.level), cb.diff(badLevelRoot.get(AgentLevel_.level), 1))
                );
        entityManager.createQuery(
                badLevelCQ.where(
//                        highAgent.isNotNull(),
                        cb.notEqual(superior, highAgent)
                )
        ).getResultList().forEach(agentLevel -> {
            AgentLevel newTop = agentLevelRepository.findTopByLoginAndLevel(agentLevel.getLogin(), agentLevel.getLevel() - 1);
            agentLevel.getSuperior().getSubAgents().remove(agentLevel);

            agentLevel.setSuperior(newTop);
            newTop.getSubAgents().add(agentLevel);
            agentLevelRepository.save(agentLevel);
        });
    }

    @Override
    public void afterInit() {
        realInit();
    }

    @Override
    public LoginRelationChangedEvent tryPromotion(LoginRelationChangedEvent event) {
        if (systemStringService.getCustomSystemString("custom.stopPromotion", null, true, Boolean.class, false)) {
            log.debug("我方系统已经停止自动升级");
            return null;
        }
        // 检查升级
        // 如果它还只是一个客户，则检查是否推荐了足够的「有效用户」
        final Login who = event.getWho();
        AgentLevel agentLevel = agentService.highestAgent(who);

        if (agentLevel != null && agentLevel.getLevel() == 0) {
            log.trace(who + "已无升级必要");
            return null;
        }

        int requireCount;
        int currentCount;
        if (agentLevel == null) {
            // 是一个客户
            requireCount = promotionCountForAgentLevel(systemService.systemLevel() - 1);
            currentCount = teamService.validCustomers(who);
            log.trace("普通用户当前数量：" + currentCount);
        } else {
            // 同级别
            requireCount = promotionCountForAgentLevel(agentLevel.getLevel() - 1);
            currentCount = teamService.agents(who, agentLevel.getLevel());
            log.trace("代理商当前数量：" + currentCount);
        }

        if (currentCount >= requireCount) {
            // 满足条件
            //
            agentLevelUpgrade(who, agentLevel);

            // 完成，顺便处理下我的推荐者
            if (who.getGuideUser() != null) {
                return new LoginRelationChangedEvent(who.getGuideUser());
            }
        } else
            log.trace(event.getWho() + "的晋升需要满足" + requireCount + ",但只有" + currentCount);
        return null;
    }

    @Override
    public AgentLevel agentLevelUpgrade(Login login, AgentLevel levelInput) {
//        contactWayService.updateName(newLogin, agentName);
//        contactWayService.updateMobile(newLogin, mobile);
//        contactWayService.updateAddress(newLogin, address);
//        contactWayService.updateIDCardImages(newLogin, cardFrontPath, cardBackPath);
        if (levelInput != null) {
            // 升级比较好处理
            // 断裂时 应当放弃所有因  之前Login,我 而产生的所有关联 既 from是之前Login,
            if (levelInput.getSuperior().getSuperior() == null || !levelInput.getSuperior().getLogin().equals(levelInput.getSuperior().getSuperior().getLogin()))
                loginRelationCacheService.breakConnection(levelInput);

            AgentLevel top = new AgentLevel();
            top.setSystem(levelInput.getSystem());
            top.setCreatedBy(null);
            top.setCreatedTime(LocalDateTime.now());
            top.setLogin(login);
            top.setRank(levelInput.getRank());
            // 使用原来上级的上级
            top.setSuperior(levelInput.getSuperior().getSuperior());
            top.setLevel(levelInput.getLevel() - 1);
            top.setSubAgents(Collections.singletonList(levelInput));
            top = agentLevelRepository.save(top);

            levelInput.setSuperior(top);
            agentLevelRepository.save(levelInput);
            // 原来跟我的关系需要复制成新的等级
            // 原来我跟其他人的关系
            loginRelationCacheService.addLowestAgentLevelCache(top);
            log.info(login + "升级到" + top.getLevel() + "代理商");
            return top;
        } else {
            //新增一个代理商！
            //应该是将它放置到它的引导者旗下
            if (login.getGuideUser() == null)
                throw new IllegalStateException("无法将一个引导者为空的身份，升级为代理商。");

            AgentLevel level = loginService.lowestAgentLevel(login.getGuideUser());
            if (level == null)
                throw new IllegalStateException("无法将一个引导者不具备代理商兴致的身份，升级为代理商。");

            AgentLevel top = new AgentLevel();
            top.setSystem(level.getSystem());
            top.setCreatedBy(null);
            top.setCreatedTime(LocalDateTime.now());
            top.setLogin(login);
            top.setRank(readService.nameForPrincipal(login));
            // 使用原来上级的上级
            top.setSuperior(level.getSuperior());
            top.setLevel(systemService.systemLevel() - 1);
            top = agentLevelRepository.save(top);
            loginRelationCacheService.addLowestAgentLevelCache(top);
            log.info(login + "升级到" + top.getLevel() + "代理商");
            return top;
        }
    }

    @Override
    public int promotionCountForAgentLevel(int level) {
        return systemStringService.getCustomSystemString("market.promotion.for" + level, "market.promotion.comment", true, Integer.class, 5);
    }

    @Override
    public void updatePromotionCountForAgentLevel(int level, int count) {
        systemStringService.updateSystemString("market.promotion.for" + level, count);
    }
}
