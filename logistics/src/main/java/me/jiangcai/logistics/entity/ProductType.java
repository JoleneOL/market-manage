package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Objects;
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
    @Column(length = 60)
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
    @Column(length = 60)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductType)) return false;
        ProductType productType = (ProductType) o;
        return Objects.equals(id, productType.getId());
    }

    /**
     * 类目下的属性
     */
    public List<PropertyName> getPropertyNameList() {
        if (propertyValueList != null) {
            return propertyValueList.stream().map(PropertyValue::getPropertyName).distinct().collect(Collectors.toList());
        }
        return null;
    }

    private List<PropertyName> getPropertyNameListBySpec(boolean spec){
        if(propertyValueList != null){
            return propertyValueList.stream().filter(p->p.getPropertyName().isSpec() == spec)
                    .map(PropertyValue::getPropertyName).distinct().collect(Collectors.toList());
        }
        return null;
    }

    public List<PropertyName> getSpecPropertyNameList(){
        return getPropertyNameListBySpec(true);
    }

    public List<PropertyName> getNoSpecPropertyNameList(){
        return getPropertyNameListBySpec(false);
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
