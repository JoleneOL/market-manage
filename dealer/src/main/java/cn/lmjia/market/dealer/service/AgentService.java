package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.service.SystemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import java.time.LocalDate;

/**
 * 代理服务
 * 应该尽量避免使用自定义函数，否者会影响Mysql的查询缓存；所以这里要准备另一份设计方案
 * <p>
 * 应当建立代理信息额外表；
 * 这个表应该是 memory engine的
 * 它拥有一个主键（同时也是用户表的外键）表明它最高达到的代理级别
 * 以及他的上级分别是哪些（AgentInfo'pk）
 * </p>
 * 它应该是根据业务需求(而绝不是技术的！）而产生更新需求要刷新重启数据库即可嘛！
 * 服务器开机时则检查该表是否存在即可
 * 每个代理系统都可以维护n个上级表
 * <ul>
 * <li>我的等级id</li>
 * <li>我的上级等级id</li>
 * <li>是否直接上级</li>
 * </ul>\
 *
 * @author CJ
 */
public interface AgentService {


    /*
当一个订单结算时，会根据它的引导者（自动成为经纪人），以及这个代理体系建立一整套分佣结果
     */


    /**
     * @param level 该身份最高可识别的代理
     * @return 该身份头衔
     */
    default String loginTitle(AgentLevel level) {
        switch (agentLevel(level)) {
            case 0:
                return "省总代理商";
            case 1:
                return "市总代理商";
            case 2:
                return "区县总代理商";
            case 3:
                return "分代理商";
            case 4:
            default:
                return "经销商";
        }
    }

    /**
     * @param agentIdExpression agentIdExpression
     * @param toExpression      toExpression
     * @param builder           builder
     * @return agentIdExpression 是否从属于toExpression(包括间接) 只有1是表示肯定的
     * @see #agentLevel(AgentLevel)
     */
    default Expression<Integer> agentBelongsExpression(Expression<?> agentIdExpression, Expression<?> toExpression
            , CriteriaBuilder builder) {
        return builder.function("mm_agentBelongs", Integer.class, agentIdExpression, toExpression);
    }

    /**
     * @param agentIdExpression agentIdExpression
     * @param builder           builder
     * @return 等级的表达式
     * @see #agentLevel(AgentLevel)
     */
    default Expression<Integer> agentLevelExpression(Expression<?> agentIdExpression, CriteriaBuilder builder) {
        return builder.function("mm_agentLevel", Integer.class, agentIdExpression);
    }

    /**
     * @param agentId 已知的一个代理的主键
     * @param builder builder
     * @return 等级的表达式
     * @see #agentLevel(AgentLevel)
     */
    default Expression<Integer> agentLevelExpression(long agentId, CriteriaBuilder builder) {
        return builder.function("mm_agentLevel", Integer.class, builder.literal(agentId));
    }

    /**
     * @param level 特定代理商
     * @return 代理等级;0 表示最高 应当存在{@link SystemService#systemLevel()}个等级
     */
    default int agentLevel(AgentLevel level) {
        AgentLevel top = topLevel(level);
        AgentLevel current = level;
        int i = 0;
        while (current != top) {
            i++;
            current = current.getSuperior();
        }
        return i;
    }

    /**
     * @param level 指定代理体系
     * @return 该体系最高等级的代理商
     */
    default AgentLevel topLevel(AgentLevel level) {
        AgentLevel current = level;
        while (current.getSuperior() != null)
            current = current.getSuperior();
        return current;
    }

    ;

    /**
     * 添加一个特定等级的代理商；按照每个代理都是同时存在的理论；那么会同时创建{@link SystemService#systemLevel()} 个代理商
     *
     * @param who          可选参数；谁添加的代理
     * @param login        相关身份
     * @param name         名称
     * @param beginDate    合同起始时间
     * @param endDate      合同结束时间
     * @param firstPayment 首笔进货款
     * @param agencyFee    代理费
     * @param superior     上级，可以为<code>null</code>
     * @return 被保存的新的最高代理商
     */
    @Transactional
    AgentLevel addAgent(Login who, Login login, String name, LocalDate beginDate, LocalDate endDate, int firstPayment, int agencyFee, AgentLevel superior);

    /**
     * 通常管理员登录显示的所有代理商；而其他代理商登录则展示自身以下的
     *
     * @param direct    是否要求直接从属
     * @param login     当前身份
     * @param agentName 可选搜索条件
     * @return login可以管理的相关代理的规格
     */
    Specification<AgentLevel> manageable(boolean direct, Login login, String agentName);

    /**
     * 通常管理员登录显示的所有代理商；而其他代理商登录则展示自身以下的
     * 并且它可以拥有下级单位
     *
     * @param direct    是否要求直接从属
     * @param login     当前身份
     * @param agentName 可选搜索条件
     * @return login可以管理的相关代理的规格
     */
    Specification<AgentLevel> manageableAndRuling(boolean direct, Login login, String agentName);

    /**
     * 通常管理员登录显示的所有代理商；而其他代理商登录则展示自身以下的
     *
     * @param login     当前身份
     * @param agentName 可选搜索条件
     * @param pageable  分页
     * @return login可以管理的相关代理
     * @see #manageable(boolean, Login, String)
     */
    @Transactional(readOnly = true)
    Page<AgentLevel> manageable(Login login, String agentName, Pageable pageable);


    /**
     * @param login 特定身份
     * @return 最高可表达的代理商
     */
    @Transactional(readOnly = true)
    AgentLevel highestAgent(Login login);

    /**
     * @param id 代理id
     * @return 代理实体；不会为null
     * @throws EntityNotFoundException 如果找不到
     */
    @Transactional(readOnly = true)
    AgentLevel getAgent(long id);
}
