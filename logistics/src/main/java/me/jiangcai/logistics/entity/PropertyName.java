package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.entity.support.PropertyType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Objects;

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
    @Column(length = 60)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyName)) return false;
        PropertyName propertyName = (PropertyName) o;
        return Objects.equals(id, propertyName.getId());
    }
}
