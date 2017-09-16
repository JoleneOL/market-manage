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
    private String path;

    /**
     * 是否有效
     */
    private boolean disabled;
    /**
     * 类目下的所有属性的属性值
     */
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private List<PropertyValue> propertyValueList;

    /**
     * 类目下的属性
     */
    public List<PropertyName> getPropertyNameList() {
        if (propertyValueList != null) {
            return propertyValueList.stream().map(PropertyValue::getPropertyName).distinct().collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 根据属性名称获取该类目下所有的属性值
     *
     * @return
     */
    public List<PropertyValue> getPropertyValueByPropertyName(PropertyName propertyName) {
        if (propertyValueList != null) {
            return propertyValueList.stream().filter(p -> p.getPropertyName().equals(propertyName)).collect(Collectors.toList());
        }
        return null;
    }


}
