package cn.lmjia.market.core.entity.financing;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * 代理商预付货款
 *
 * @author CJ
 */
@Setter
@Getter
@Entity
public class AgentGoodAdvancePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Login login;

    /**
     * 具体的操作者，可控
     */
    @ManyToOne
    private Manager operator;

    /**
     * 发生时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime happenTime;


}
