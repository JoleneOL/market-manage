package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.MainProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface MainProductRepository extends JpaRepository<MainProduct, String>, JpaSpecificationExecutor<MainProduct> {
    MainProduct findByName(String name);
}
