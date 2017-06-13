package cn.lmjia.market.core.entity.deal;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.pk.OrderCommissionPK;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import java.time.LocalDateTime;

/**
 * 一个订单产生的佣金记录
 * 必须保证一个订单就一个事情只能做一次！
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@IdClass(OrderCommissionPK.class)
public class OrderCommission {
    /**
     * 佣金来源订单
     * 如果这个订单产生退款或者什么什么的，这个佣金也应当被退回
     */
    @Id
    @ManyToOne(optional = false)
    private MainOrder source;
    /**
     * 是否退款
     */
    @Id
    private boolean refund;
    /**
     * 佣金产生时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime generateTime;

    /**
     * @param builder             cb
     * @param orderCommissionFrom from
     * @return 可以描述成为id的表达式
     */
    public static Expression<String> getIdSelection(CriteriaBuilder builder, From<?, OrderCommission> orderCommissionFrom) {
        return builder.concat(
                builder.concat(
                        orderCommissionFrom.get("source").get("id").as(String.class)
                        , "-")
                , orderCommissionFrom.get("refund")
        );
    }


}
