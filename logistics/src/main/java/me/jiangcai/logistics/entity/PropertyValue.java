package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * 描述货品性质、规格的参数
 * Created by helloztt on 2017/9/12.
 */
@Setter
@Getter
@Entity
@ToString
public class PropertyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 参数值
     */
    private String value;
    /**
     * 属性
     */
    @ManyToOne
    private PropertyName propertyName;
    /**
     * 默认图标
     */
    private String icon;
    /**
     * 排序
     */
    private int weight;
    /**
     * 是否无效
     */
    private boolean disabled;
}
