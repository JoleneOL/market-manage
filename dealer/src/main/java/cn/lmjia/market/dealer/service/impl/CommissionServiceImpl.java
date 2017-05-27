package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.dealer.entity.Commission;
import cn.lmjia.market.dealer.service.CommissionService;
import me.jiangcai.lib.spring.data.AndSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author CJ
 */
@Service
public class CommissionServiceImpl implements CommissionService {
    @Override
    public Specification<Commission> listSpecification(Login login, Specification<Commission> specification) {
        return new AndSpecification<>(new Specification<Commission>() {
            @Override
            public Predicate toPredicate(Root<Commission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query = query.groupBy(root.get("orderCommission"));
                return cb.equal(root.get("who"), login);
            }
        }, specification);
    }
}
