package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.wx.standard.entity.StandardWeixinUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * 表明这是一个可登录的法人或者自然人
 *
 * @author CJ
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"loginName", "code"})})
public class Login implements UserDetails {

    /**
     * 可以管理所有关于代理的项目
     */
    public static final String ROLE_AllAgent = "ALL_AGENT";
    /**
     * 仅仅查看经销商
     */
    public static final String ROLE_SERVICE = "SERVICE";
//    /**
//     * 代理商升级管理权限
//     */
//    public static final String ROLE_PROMOTION = "PROMOTION";
    /**
     * 公司内部管理员???
     */
    public static final String ROLE_MANAGER = "MANAGER";
    /**
     * 可以管理员工列表；没有这个权限则只可以查看???
     */
    public static final String ROLE_GRANT = "GRANT";
    /**
     * 后台数据查看权限, 可以查看所有后台数据,但是无法操作.
     */
    public static final String ROLE_LOOK = "LOOK";
    /**
     * 订单管理权限
     */
    public static final String ROLE_ALL_ORDER = "ALL_ORDER";
    /**
     * 财务代理商佣金管理提现管理
     */
    public static final String ROLE_FINANCE = "FINANCE";
    /**
     * 产品中心管理权限
     */
    public static final String ROLE_PRODUCT_CENTER = "PRODUCT_CENTER";
    /**
     * 供应链管理权限(包含物流管理权限)
     */
    public static final String ROLE_SUPPLY_CHAIN = "SUPPLY_CHAIN";
    /**
     * 物流管理权限
     */
    public static final String ROLE_LOGISTICS = "LOGISTICS";
//    /**
//     * 商城后台管理权限(目前没有用)
//     */
//    public static final String ROLE_MALL = "MALL";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 30)
    private String loginName;
    /**
     * 推荐码，需要唯一；
     * TODO 算法待定
     */
    private String code;
    private String password;
    private boolean enabled = true;
    /**
     * 添加时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime createdTime;
    /**
     * 引导者
     */
    @ManyToOne
    private Login guideUser;
    /**
     * 是否被修改锅引导者
     */
    private boolean guideChanged = false;
    @OneToOne(cascade = CascadeType.ALL)
    private ContactWay contactWay;
    /**
     * 这个身份所关联的用户，通常应该是唯一的
     */
    @OneToOne
    private StandardWeixinUser wechatUser;

    // 财务有关
    /**
     * 当前货款余额
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal currentGoodPayment = BigDecimal.ZERO;
    /**
     * 佣金余额，结算佣金余额
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal commissionBalance = BigDecimal.ZERO;

    /**
     * 正式用户的标志(爱心天使)
     *
     * @since {@link cn.lmjia.market.core.Version#newLogin}
     */
    private boolean successOrder;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Login)) return false;
        Login login = (Login) o;
        if (id != null)
            return Objects.equals(id, login.id);
        return Objects.equals(loginName, login.loginName);
    }

    @Override
    public int hashCode() {
        if (id != null)
            return Objects.hash(id);
        return Objects.hash(loginName);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + ROLE_AllAgent));
        return Collections.emptySet();
    }

    @Override
    public String getUsername() {
        return loginName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isManageable() {
        return false;
    }

    /**
     * @return 登录身份的头衔
     */
    public String getLoginTitle() {
        return "一般";
    }

    @Override
    public String toString() {
        return "Login{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", enabled=" + enabled +
                ", guideUser=" + guideUser +
                '}';
    }

    /**
     * 避免循环引导，即我不应该是目标用户引导链上的
     *
     * @param target 目标用户
     * @return 目标用户是否可以成为当前用户的引导者
     */
    public boolean isGuideAble(Login target) {
        if (this.equals(target)) {
            return false;
        }
        if (target.getGuideUser() == null)
            return true;
        if (target.getGuideUser().equals(this))
            return false;
        return isGuideAble(target.getGuideUser());
    }

    public boolean isRoot() {
        return getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ROOT"));
    }
}
