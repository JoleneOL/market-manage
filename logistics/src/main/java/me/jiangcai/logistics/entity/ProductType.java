package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 货品分类
 * Created by helloztt on 2017/9/12.
 */
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
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
    private boolean parent;
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
    /**
     * 类目下的属性值
     */
    @ElementCollection
    private List<PropertyValue> propertyValueList;

    /**
     * 类目下的属性
     */
    private List<PropertyName> getPropertyNameList(){
        if(propertyValueList != null){
            return propertyValueList.stream().map(PropertyValue::getPropertyName).distinct().collect(Collectors.toList());
        }
        return null;
    }



}
