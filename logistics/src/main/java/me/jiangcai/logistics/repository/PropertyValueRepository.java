package me.jiangcai.logistics.repository;

import me.jiangcai.logistics.entity.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by helloztt on 2017/9/13.
 */
public interface PropertyValueRepository extends JpaRepository<PropertyValue,Long>,JpaSpecificationExecutor<PropertyValue> {
}
