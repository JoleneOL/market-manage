package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.Login;
import org.springframework.transaction.annotation.Transactional;

/**
 * 我的团队
 *
 * @author CJ
 */
public interface TeamService {

//    /**
//     * @param login 身份
//     * @return 推荐客户数量
//     */
//    @Transactional(readOnly = true)
//    int customers(Login login);

    /**
     * 推荐的爱心天使数量
     *
     * @param login 身份
     * @return 推荐 <a href="https://github.com/JoleneOL/market-manage/wiki/%E5%AE%9A%E4%B9%89#%E6%9C%89%E6%95%88%E7%94%A8%E6%88%B7">有效客户</a> 数量
     */
    @Transactional(readOnly = true)
    int validCustomers(Login login);

    /**
     * @param login 身份
     * @param level 代理商等级
     * @return 推荐特定代理商数量
     */
    @Transactional(readOnly = true)
    int agents(Login login, int level);

    /**
     * @param login 身份
     * @return 所有推荐总量
     */
    @Transactional(readOnly = true)
    int all(Login login);

}
