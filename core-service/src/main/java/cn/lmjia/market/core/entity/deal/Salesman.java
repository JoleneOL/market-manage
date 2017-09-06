package cn.lmjia.market.core.entity.deal;

import cn.lmjia.market.core.entity.Login;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

/**
 * 推销人员，同时也是一个身份
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@SuppressWarnings("unused")
public class Salesman {
    @OneToOne
    @Id
    private Login login;

    /**
     * 销售奖励提成，必须小于1
     */
    @Column(scale = 8, precision = 10)
    private BigDecimal salesRate;

}
