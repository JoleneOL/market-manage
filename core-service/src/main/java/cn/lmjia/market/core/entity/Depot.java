package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.entity.support.Address;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Depot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 上架状态
     */
    private boolean enable;

    @Column(columnDefinition = "timestamp")
    private LocalDateTime createTime;

    @Column(length = 100)
    private String name;
    /**
     * 海尔（日日顺）仓库编码：按日日顺C码
     */
    @Column(length = 32)
    private String haierCode;

    private Address address;

}
