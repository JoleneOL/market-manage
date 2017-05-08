package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author CJ
 */
public interface LoginService extends UserDetailsService {
    /**
     * @return 所有管理员
     */
    @Transactional(readOnly = true)
    List<Manager> managers();

    /**
     * 更新密码
     *
     * @param login       一个登录
     * @param rawPassword 明文密码
     * @return 已被保存的登录
     */
    @Transactional
    <T extends Login> T password(T login, String rawPassword);

    Login get(long id);

    /**
     * 新增普通登录
     *
     * @param username    登录名
     * @param guide       引导者
     * @param rawPassword 明文密码
     * @return
     */
    @Transactional
    Login newLogin(String username, Login guide, String rawPassword);
}
