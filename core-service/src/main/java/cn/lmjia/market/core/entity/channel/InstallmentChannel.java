package cn.lmjia.market.core.entity.channel;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * 分期付款的渠道
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class InstallmentChannel extends Channel {

    /**
     * 相比较货款所产生的手续费，支持0
     */
    @Column(scale = 9, precision = 10)
    private BigDecimal poundageRate = BigDecimal.ZERO;

}
