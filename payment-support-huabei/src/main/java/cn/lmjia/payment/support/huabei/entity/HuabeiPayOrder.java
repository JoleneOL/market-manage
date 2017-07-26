package cn.lmjia.payment.support.huabei.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.payment.entity.PayOrder;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class HuabeiPayOrder extends PayOrder {

    @Column(length = 30)
    private String tradeNo;
    @Column(length = 20)
    private String tradeCode;
    @Column(length = 30)
    private String buyer;
}
