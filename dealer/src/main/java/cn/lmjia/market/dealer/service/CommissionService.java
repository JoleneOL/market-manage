package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.dealer.entity.Commission;
import org.springframework.data.jpa.domain.Specification;

/**
 * 佣金查询之类的活儿
 *
 * @author CJ
 */
public interface CommissionService {


    /**
     * 查询
     *
     * @param login         当前身份
     * @param specification 既定规则
     * @return 佣金记录的规格
     */
    Specification<Commission> listSpecification(Login login, Specification<Commission> specification);
}
