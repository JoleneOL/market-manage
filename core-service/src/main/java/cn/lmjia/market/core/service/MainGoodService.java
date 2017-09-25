package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.channel.Channel;
import me.jiangcai.logistics.entity.PropertyValue;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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
//    @Transactional(readOnly = true)
    List<MainGood> forSale(Channel channel);

    /**
     * 特定渠道中符合标签的在售商品列表
     *
     * @param channel 可选的特定渠道
     * @param tags    标签数组
     * @return
     */
    //    @Transactional(readOnly = true)
    List<MainGood> forSale(Channel channel, String... tags);

    /**
     * 特定渠道中某标签下在上商品列表所用到的属性值
     * @param channel
     * @param tag
     * @return
     */
    Set<String> forSalePropertyValue(Channel channel,String tag);

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
