package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface ProductTypeRepository extends JpaRepository<ProductType, String>, JpaSpecificationExecutor<ProductType> {
}
