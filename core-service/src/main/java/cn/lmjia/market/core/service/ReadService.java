package cn.lmjia.market.core.service;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import org.springframework.transaction.annotation.Transactional;

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
     * @param loginPath       登录者
     * @param criteriaBuilder cb
     * @return {@link #nameForPrincipal(Object)}
     */
    static Expression<String> mobileForLogin(From<?, Login> loginPath, CriteriaBuilder criteriaBuilder) {
        Join<Login, ContactWay> contactWayJoin = loginPath.join("contactWay", JoinType.LEFT);
        Expression<String> loginName = loginPath.get("loginName");
        Expression<String> name = contactWayJoin.get("mobile");
        //
        return JpaFunctionUtils.ifNull(criteriaBuilder, String.class, name
                , JpaFunctionUtils.ifElse(criteriaBuilder, String.class, criteriaBuilder.greaterThan(criteriaBuilder.length(name), 0), name, loginName));
    }

    static Expression<Integer> agentLevelForLogin(From<?, Login> loginPath, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.function("LoginAgentLevel", Integer.class, loginPath.get("id"));
    }

    /**
     * @param loginPath       登录者
     * @param criteriaBuilder cb
     * @return {@link #nameForPrincipal(Object)}
     */
    static Expression<String> nameForLogin(From<?, Login> loginPath, CriteriaBuilder criteriaBuilder) {
        Join<Login, ContactWay> contactWayJoin = loginPath.join("contactWay", JoinType.LEFT);
        Expression<String> loginName = loginPath.get("loginName");
        Expression<String> name = contactWayJoin.get("name");
        //
        return JpaFunctionUtils.ifNull(criteriaBuilder, String.class, name
                , JpaFunctionUtils.ifElse(criteriaBuilder, String.class, criteriaBuilder.greaterThan(criteriaBuilder.length(name), 0), name, loginName));
    }

    /**
     * @param customerFrom    登录者
     * @param criteriaBuilder cb
     * @return {@link #nameForPrincipal(Object)}
     */
    static Expression<String> nameForCustomer(From<?, Customer> customerFrom, CriteriaBuilder criteriaBuilder) {
        Expression<String> name = customerFrom.get("name");
        return JpaFunctionUtils.ifNull(criteriaBuilder, String.class, name
                , nameForLogin(customerFrom.join("login"), criteriaBuilder));
    }

    /**
     * @param i 登录者级别；可以视作代理级别
     * @return 登录标题
     */
    default String getLoginTitle(int i) {
        switch (i) {
            case 0:
                return "全球委托";
            case 1:
                return "超级代理";
            case 2:
                return "市级代理";// 5% 1% 区域服务费 1%
            case 3:
                return "区县代理";// 5% 1% 未来再实现的1%
            case 4:
                return "经销商";// 5% 1%
            case Customer.LEVEL:
                return "爱心天使";
            case Customer.LEVEL * 2:
                return "普通用户";
            default:
                return "经销商";
        }
    }

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
     * @param principal 身份；通常是一个{@link cn.lmjia.market.core.entity.Login}
     * @return 微信昵称；如果没有则使用{@link #nameForPrincipal(Object)}
     */
    String wechatNickNameForPrincipal(Object principal);

    /**
     * @param principal 身份；通常是一个{@link cn.lmjia.market.core.entity.Login}
     * @return 可选的地址
     */
    Address addressFor(Object principal);

    /**
     * 以百分比的方式显示数字；如有必要将最多保留2位小数
     *
     * @param input 数字
     * @return 诸如 20%或者10.15%
     */
    String percentage(BigDecimal input);

    /**
     * @param principal 身份，一般是Login
     * @return 可提现余额
     */
    @Transactional(readOnly = true)
    Money currentBalance(Object principal);

    /**
     * @param principal 身份，一般是Login
     * @return 头像
     */
    @Transactional(readOnly = true)
    String avatarFor(Object principal);

    /**
     * @param customer 客户
     * @return 客户的年纪
     */
    int ageForCustomer(Customer customer);

    /**
     * @param principal 身份，一般是Login
     * @return 代理级别
     */
    @Transactional(readOnly = true)
    int agentLevelForPrincipal(Object principal);

    /**
     * @return 所有等级名称
     */
    @SuppressWarnings("unused")
    String[] titles();
}
