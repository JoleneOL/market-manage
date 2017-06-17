package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.Commission;
import org.springframework.data.jpa.domain.Specification;

/**
 * 佣金查询之类的活儿
 *
 * @author CJ
 */
public interface CommissionService {

    /**
     * @param login         当前身份
     * @param specification 既定规则
     * @return 属于当前身份真实可用佣金记录的规格
     */
    Specification<Commission> listRealitySpecification(Login login, Specification<Commission> specification);

    /**
     * @param login         当前身份
     * @param specification 既定规则
     * @return 属于当前身份所有佣金记录的规格
     */
    Specification<Commission> listAllSpecification(Login login, Specification<Commission> specification);
}
