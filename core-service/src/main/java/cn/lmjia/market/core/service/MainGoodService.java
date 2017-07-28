package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.MainGood;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 主要商品服务
 *
 * @author CJ
 */
public interface MainGoodService {
    /**
     * @param fixedPrice 可选的特定金额
     * @return 在售商品列表
     */
    @Transactional(readOnly = true)
    List<MainGood> forSale(BigDecimal fixedPrice);

    /**
     * @return 在售商品列表
     */
    @Transactional(readOnly = true)
    List<MainGood> forSale();
}
