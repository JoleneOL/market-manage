package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.jpa.entity.support.Address;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
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

    private Address address;
    /**
     * 负责人姓名
     */
    @Column(length = 20)
    private String chargePeopleName;
    /**
     * 负责人电话
     */
    @Column(length = 20)
    private String chargePeopleMobile;

    /**
     * 因为JPA实现的BUG
     * 默认0
     */
    private byte _type;

}
