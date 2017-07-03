package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
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

import java.time.LocalDateTime;

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

    @Override
    public LoginRelationChangedEvent tryPromotion(LoginRelationChangedEvent event) {
        if (systemStringService.getSystemString("custom.stopPromotion", Boolean.class, false)) {
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
        } else {
            // 同级别
            requireCount = promotionCountForAgentLevel(agentLevel.getLevel() - 1);
            currentCount = teamService.agents(who, agentLevel.getLevel());
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

    /**
     * 代理商晋升
     * 如果当前还没有代理商身份，则晋升成为推荐者的下线代理商
     *
     * @param login 身份
     * @param level 当前代理
     */
    private void agentLevelUpgrade(Login login, AgentLevel level) {
//        contactWayService.updateName(newLogin, agentName);
//        contactWayService.updateMobile(newLogin, mobile);
//        contactWayService.updateAddress(newLogin, address);
//        contactWayService.updateIDCardImages(newLogin, cardFrontPath, cardBackPath);
        if (level != null) {
            // 升级比较好处理
            // 断裂时 应当放弃所有因  之前Login,我 而产生的所有关联 既 from是之前Login,
            if (level.getSuperior().getSuperior() == null || !level.getSuperior().getLogin().equals(level.getSuperior().getSuperior().getLogin()))
                loginRelationCacheService.breakConnection(level);

            AgentLevel top = new AgentLevel();
            top.setSystem(level.getSystem());
            top.setCreatedBy(null);
            top.setCreatedTime(LocalDateTime.now());
            top.setLogin(login);
            top.setRank(level.getRank());
            // 使用原来上级的上级
            top.setSuperior(level.getSuperior().getSuperior());
            top.setLevel(level.getLevel() - 1);
            top = agentLevelRepository.save(top);

            level.setSuperior(top);
            // 原来跟我的关系需要复制成新的等级
            // 原来我跟其他人的关系
            loginRelationCacheService.addLowestAgentLevelCache(top);
            log.info(login + "升级到" + top.getLevel() + "代理商");
        } else {
            //新增一个代理商！
            //应该是将它放置到它的引导者旗下
            if (login.getGuideUser() == null)
                throw new IllegalStateException("无法将一个引导者为空的身份，升级为代理商。");

            level = loginService.lowestAgentLevel(login.getGuideUser());
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
        }
    }

    @Override
    public int promotionCountForAgentLevel(int level) {
        return systemStringService.getSystemString("market.promotion.for" + level, Integer.class, 5);
    }

    @Override
    public void updatePromotionCountForAgentLevel(int level, int count) {
        systemStringService.updateSystemString("market.promotion.for" + level, count);
    }
}
