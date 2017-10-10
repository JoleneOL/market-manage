package me.jiangcai.logistics.repository;

import me.jiangcai.logistics.entity.PropertyName;
import me.jiangcai.logistics.entity.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by helloztt on 2017/9/13.
 */
public interface PropertyValueRepository extends JpaRepository<PropertyValue, Long>, JpaSpecificationExecutor<PropertyValue> {
    /**
     * 由属性和属性值名称来决定一个属性值
     *
     * @param propertyName 属性
     * @param value        属性值名称
     * @return
     */
    PropertyValue findTop1ByPropertyNameAndValue(PropertyName propertyName, String value);
}
