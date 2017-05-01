package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 联系方式
 *
 * @author CJ
 */
@Getter
@Setter
@Entity
public class ContactWay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20)
    private String mobile;
    /**
     * 联系人
     */
    @Column(length = 50)
    private String name;

}
