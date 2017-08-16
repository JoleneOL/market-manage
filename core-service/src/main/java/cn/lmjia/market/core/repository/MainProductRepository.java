package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.MainProduct;
import me.jiangcai.logistics.repository_util.AbstractProductRepository;

/**
 * @author CJ
 */
public interface MainProductRepository extends AbstractProductRepository<MainProduct> {
    MainProduct findByName(String name);
}
