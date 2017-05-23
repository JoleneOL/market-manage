package cn.lmjia.market.core.entity.deal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * 每一个等级的奖励设置
 *
 * @author CJ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AgentRate {
    /**
     * 销售奖励提成
     */
    @Column(scale = 8, precision = 10)
    private BigDecimal marketRate;
    /**
     * 推荐奖励提成
     */
    @Column(scale = 8, precision = 10)
    private BigDecimal recommendRate;
}
