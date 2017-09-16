package me.jiangcai.logistics.repository;

import me.jiangcai.logistics.entity.ProductType;
import me.jiangcai.logistics.entity.support.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by helloztt on 2017/9/13.
 */
public interface ProductTypeRepository extends JpaRepository<ProductType, Long>, JpaSpecificationExecutor<PropertyType> {
    ProductType findTop1ByName(String name);
}
