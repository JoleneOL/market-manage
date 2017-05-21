package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.jpa.JpaUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.math.BigDecimal;

/**
 * 获取信息服务
 * 通常都是一些非常简单的
 *
 * @author CJ
 */
public interface ReadService {

    /**
     * @param principal 身份；通常是一个{@link cn.lmjia.market.core.entity.Login}
     * @return 手机号码；或者一个空字符串
     */
    String mobileFor(Object principal);

    /**
     * @param principal 身份；通常是一个{@link cn.lmjia.market.core.entity.Login}
     * @return 名字；或者登录名
     */
    String nameForPrincipal(Object principal);

    /**
     * @param loginPath       登录者
     * @param criteriaBuilder cb
     * @return {@link #nameForPrincipal(Object)}
     */
    default Expression<String> nameForLogin(From<?, Login> loginPath, CriteriaBuilder criteriaBuilder) {
        Join<Login, ContactWay> contactWayJoin = loginPath.join("contactWay", JoinType.LEFT);
        Expression<String> loginName = loginPath.get("loginName");
        Expression<String> name = contactWayJoin.get("name");
        //
        return JpaUtils.ifNull(criteriaBuilder, String.class, name
                , JpaUtils.ifElse(criteriaBuilder, String.class, criteriaBuilder.greaterThan(criteriaBuilder.length(name), 0), name, loginName));
    }

    /**
     * 以百分比的方式显示数字；如有必要将最多保留2位小数
     *
     * @param input 数字
     * @return 诸如 20%或者10.15%
     */
    String percentage(BigDecimal input);

}
