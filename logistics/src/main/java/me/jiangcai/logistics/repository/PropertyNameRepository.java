package me.jiangcai.logistics.repository;

import me.jiangcai.logistics.entity.PropertyName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by helloztt on 2017/9/13.
 */
public interface PropertyNameRepository extends JpaRepository<PropertyName,Long>,JpaSpecificationExecutor<PropertyName> {

    PropertyName findTop1ByName(String name);
}
