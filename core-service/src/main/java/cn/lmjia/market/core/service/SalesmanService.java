package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.Salesman;
import org.springframework.transaction.annotation.Transactional;

/**
 * 销售人员计划
 *
 * @author CJ
 */
public interface SalesmanService {
    /**
     * 销售人员salesmanId刚刚推荐了login
     *
     * @param salesmanId
     * @param login
     */
    @Transactional
    void salesmanShareTo(long salesmanId, Login login);

    @Transactional(readOnly = true)
    Salesman get(long id);
}
