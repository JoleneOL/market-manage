package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * 代理服务
 *
 * @author CJ
 */
public interface AgentService {

    /**
     * @return 代理体系的等级
     */
    default int systemLevel() {
        return 3;
    }

    /**
     * @param level 该身份最高可表达的代理
     * @return 代理等级;0 表示最高 应当存在{@link #systemLevel()}个等级
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
     * 添加一个最顶级的代理商；按照每个代理都是同时存在的理论；那么会同时创建{@link #systemLevel()}个代理商
     *
     * @param login 相关联的登录
     * @param name  名称
     * @return 被保存的最顶级的代理商
     */
    @Transactional
    AgentLevel addTopAgent(Login login, String name);

    /**
     * 添加一个特定等级的代理商；按照每个代理都是同时存在的理论；那么会同时创建{@link #systemLevel()}个代理商
     *
     * @param login    相关身份
     * @param name     名称
     * @param superior 上级
     * @return 被保存的新的最高代理商
     */
    @Transactional
    AgentLevel addAgent(Login login, String name, AgentLevel superior);

    /**
     * @param level 该身份最高可识别的代理
     * @return 该身份头衔
     */
    default String loginTitle(AgentLevel level) {
        switch (agentLevel(level)) {
            case 0:
                return "总代理商";
            case 1:
                return "分代理商";
            case 2:
            default:
                return "经销商";
        }
    }

    /**
     * 通常管理员登录显示的所有代理商；而其他代理商登录则展示自身以下的
     *
     * @param login    当前身份
     * @param pageable 分页
     * @return login可以管理的相关代理
     */
    @Transactional(readOnly = true)
    Page<AgentLevel> manageable(Login login, Pageable pageable);


    /**
     * @param login 特定身份
     * @return 最高可表达的代理商
     */
    @Transactional(readOnly = true)
    AgentLevel highestAgent(Login login);
}
