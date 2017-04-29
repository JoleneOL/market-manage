package cn.lmjia.market.core.util;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface AbstractAgentLevelRepository<T extends AgentLevel>
        extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    /**
     * 根据身份获取代理信息
     *
     * @param login 登录者
     * @return 必须唯一的代理信息
     */
    T findByLogin(Login login);

}
