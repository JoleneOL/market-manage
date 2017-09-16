package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.jiangcai.logistics.entity.support.PropertyType;

import javax.persistence.*;
import java.util.List;

/**
 * 描述货品性质、规格的参数
 * Created by helloztt on 2017/9/12.
 */
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@ToString
public class PropertyName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 参数名称
     */
    private String name;
    /**
     * 参数类型
     */
    private PropertyType type;
    /**
     * 是否作为一个规格,默认为是
     */
    private boolean spec;
    /**
     * 是否是必填项，默认为是
     */
    private boolean required = true;
    /**
     * 是否是数字
     */
    private boolean number;
    /**
     * 排序
     */
    private int weight;
    /**
     * 是否无效
     */
    private boolean disabled;
    /**
     * 规格值
     */
    @OneToMany(cascade = {CascadeType.ALL})
    private List<PropertyValue> propertyValueList;

}
