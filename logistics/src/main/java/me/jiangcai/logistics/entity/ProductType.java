package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 货品分类
 * Created by helloztt on 2017/9/12.
 */
@Setter
@Getter
@Entity
public class ProductType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 类型名称
     */
    private String name;

    /**
     * 是否有子类目
     */
    private boolean isParent;
    /**
     * 上级类目
     */
    @ManyToOne
    private ProductType parentProductType;
    /**
     * 类目路径
     */
    private String  path;

    /**
     * 是否有效
     */
    private boolean disabled;



}
