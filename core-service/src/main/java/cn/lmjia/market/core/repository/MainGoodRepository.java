package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface MainGoodRepository extends JpaRepository<MainGood, Long>, JpaSpecificationExecutor<MainGood> {

    MainGood findByProduct(MainProduct product);
}
