package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.wx.standard.entity.StandardWeixinUser;
import org.springframework.security.core.GrantedAuthority;
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
     * 升级代理
     */
    public static final String ROLE_PROMOTION = "PROMOTION";
    /**
     * 公司内部管理员
     */
    public static final String ROLE_MANAGER = "MANAGER";
    /**
     * 可以管理员工列表；没有这个权限则只可以查看
     */
    public static final String ROLE_GRANT = "GRANT";
    /**
     * 可以管理订单
     */
    public static final String ROLE_ALL_ORDER = "ALL_ORDER";

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
}
