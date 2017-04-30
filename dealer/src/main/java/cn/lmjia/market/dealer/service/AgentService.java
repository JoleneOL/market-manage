package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
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
     * 添加一个最顶级的代理商；按照每个代理都是同时存在的理论；那么会同时创建{@link #systemLevel()}个代理商
     *
     * @param login 相关联的登录
     * @param name  名称
     * @return 被保存的最顶级的代理商
     */
    @Transactional
    AgentLevel addTopAgent(Login login, String name);
}
