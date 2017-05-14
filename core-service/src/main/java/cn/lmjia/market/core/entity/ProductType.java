package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 具体产品类型
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class ProductType {

    @Id
    private String id;
    @ManyToOne
    private Product product;
    @Column(length = 40)
    private String name;
}
