package cn.lmjia.market.core.service;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainProduct;
import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.financing.AgentGoodAdvancePayment;
import cn.lmjia.market.core.entity.financing.AgentGoodAdvancePayment_;
import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder;
import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder_;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.entity.support.TagType;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.ProductType;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.util.List;

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
//                , JpaFunctionUtils.ifElse(criteriaBuilder, String.class, criteriaBuilder.greaterThan(criteriaBuilder.length(name), 0), name, loginName));
                , loginName);
    }

    static Expression<Integer> agentLevelForLogin(From<?, Login> loginPath, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.function("LoginAgentLevel", Integer.class, loginPath.get("id"));
    }

    /**
     * @param loginPath       登录者
     * @param criteriaBuilder cb
     * @return {@link #nameForPrincipal(Object)}
     */
    static Expression<String> nameForLogin(From<?, ? extends Login> loginPath, CriteriaBuilder criteriaBuilder) {
        Join<Login, ContactWay> contactWayJoin = loginPath.join("contactWay", JoinType.LEFT);
        Expression<String> loginName = loginPath.get("loginName");
        Expression<String> name = contactWayJoin.get("name");
        //
        return JpaFunctionUtils.ifNull(criteriaBuilder, String.class, name
//                , JpaFunctionUtils.ifElse(criteriaBuilder, String.class, criteriaBuilder.greaterThan(criteriaBuilder.length(name), 0), name, loginName));
                , loginName);
    }

//    /**
//     * @param customerFrom    登录者
//     * @param criteriaBuilder cb
//     * @return {@link #nameForPrincipal(Object)}
//     */
//    static Expression<String> nameForCustomer(From<?, Customer> customerFrom, CriteriaBuilder criteriaBuilder) {
//        Expression<String> name = customerFrom.get("name");
//        return JpaFunctionUtils.ifNull(criteriaBuilder, String.class, name
//                , nameForLogin(customerFrom.join(Customer_.login), criteriaBuilder));
//    }

    static Expression<BigDecimal> currentGoodAdvancePaymentBalance(Expression<? extends Login> loginFrom
            , CriteriaBuilder cb, CriteriaQuery<?> cq) {
        Subquery<BigDecimal> add = cq.subquery(BigDecimal.class);
        Root<AgentGoodAdvancePayment> root = add.from(AgentGoodAdvancePayment.class);
        add = add
                .select(cb.sum(root.get(AgentGoodAdvancePayment_.amount)))
                .where(
                        cb.equal(root.get(AgentGoodAdvancePayment_.login), loginFrom)
                        , AgentGoodAdvancePayment.isSuccessPayment(root, cb)
                );

        Subquery<BigDecimal> ordered = cq.subquery(BigDecimal.class);

        // 减去非关闭的订单
        Root<AgentPrepaymentOrder> orderRoot = ordered.from(AgentPrepaymentOrder.class);
        ordered = ordered.select(cb.sum(orderRoot.get(AgentPrepaymentOrder_.goodTotalPriceAmountIndependent)))
                .where(cb.and(
                        cb.equal(orderRoot.get(AgentPrepaymentOrder_.belongs), loginFrom)
                        , cb.notEqual(orderRoot.get(AgentPrepaymentOrder_.orderStatus), OrderStatus.close)
                ));

        return cb.diff(add, ordered);
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
     * @param agentLevel 代理商
     * @return 一个代理商的完整名称
     */
    String nameForAgent(AgentLevel agentLevel);

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
     * @param delimiter 链接字符
     * @return 用delimiter连接principal的曾用名
     */
    String joinUsedNamesForPrincipal(Object principal, CharSequence delimiter);

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
     * @return 可用的货款余额
     */
    @Transactional(readOnly = true)
    Money currentGoodAdvancePaymentBalance(Object principal);

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
     * @return 可用仓库
     */
    @Transactional(readOnly = true)
    List<Depot> allEnabledDepot();

    /**
     * @return 可用主要货品
     */
    @Transactional(readOnly = true)
    List<MainProduct> allEnabledMainProduct();

    /**
     * @return 所有渠道
     */
    @Transactional(readOnly = true)
    List<Channel> allChannel();

    /**
     * @return 可用主要货品
     */
    @Transactional(readOnly = true)
    List<Product> allEnabledProduct();


    /**
     * @return 所有等级名称
     */
    @SuppressWarnings("unused")
    String[] titles();

    /**
     * 所有货品类型
     *
     * @return
     */
    @Transactional(readOnly = true)
    List<ProductType> allProductType();

    /**
     * 所有标签类型
     *
     * @return
     */
    TagType[] allTagType();

    /**
     * 所有可用标签
     *
     * @return
     */
    @Transactional(readOnly = true)
    List<Tag> allEnabledTag();

    @Transactional(readOnly = true)
    List<Tag> allTagByType(int tagType);
}
