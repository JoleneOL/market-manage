package cn.lmjia.market.core.entity.deal;

import cn.lmjia.market.core.entity.Login;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 推销人员，同时也是一个身份；
 * 如果一个非关注也非用户扫码之后:
 * <ul>
 * <li>GET wechatJoinSM</li>
 * <li>引导微信扫码 SF_SM_id</li>
 * <li>临时促销表增加记录id-current 时效为10m;即扫码关注之后在10m内打开下单页面即被认为是由id促销导致的</li>
 * <li>关注之后呢？这里的业务被中断了 得用户自行点击下单才可以继续</li>
 * <li>GET wechatJoin</li>
 * <li>注册</li>
 * <li>GET order</li>
 * </ul>
 * 一个已关注非用户
 * <ul>
 * <li>GET wechatJoinSM</li>
 * <li>临时促销表增加记录id-current 时效为10m;即扫码关注之后在10m内打开下单页面即被认为是由id促销导致的</li>
 * <li>GET wechatJoin</li>
 * <li>注册</li>
 * <li>GET order</li>
 * </ul>
 * 一个已关注的用户
 * <ul>
 * <li>GET wechatJoinSM</li>
 * <li>临时促销表增加记录id-current 时效为10m;即扫码关注之后在10m内打开下单页面即被认为是由id促销导致的</li>
 * <li>GET wechatJoin</li>
 * <li>GET order</li>
 * </ul>
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@ToString
public class Salesman {
    @Id
    private Long id;
    @OneToOne
    @MapsId
    private Login login;

    private boolean enable;

    /**
     * 销售奖励提成，必须小于1
     */
    @Column(scale = 8, precision = 10)
    private BigDecimal salesRate;

    @Column(length = 100)
    private String rank;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Salesman)) return false;
        Salesman salesman = (Salesman) o;
        return Objects.equals(login, salesman.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}
