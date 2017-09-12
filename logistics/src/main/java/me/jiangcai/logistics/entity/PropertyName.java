package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;
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
     * 是否作为一个规格
     */
    private boolean isSpec;
    /**
     * 是否是必填项
     */
    private boolean isRequired;
    /**
     * 是否是数字
     */
    private boolean isNumber;
    /**
     * 排序
     */
    private int order;
    /**
     * 是否有效
     */
    private boolean disabled;
    /**
     * 规格值
     */
    @ElementCollection
    private List<PropertyName> propertyNameList;

}
