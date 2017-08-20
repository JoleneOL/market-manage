package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.channel.Channel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 主要商品服务
 *
 * @author CJ
 */
public interface MainGoodService {

    /**
     * @param channel 可选的特定渠道
     * @return 在售商品列表
     */
    @Transactional(readOnly = true)
    List<MainGood> forSale(Channel channel);


    /**
     * 默认渠道
     *
     * @return 在售商品列表
     */
    @Transactional(readOnly = true)
    List<MainGood> forSale();

    @Transactional(readOnly = true)
    void priceCheck();
}
