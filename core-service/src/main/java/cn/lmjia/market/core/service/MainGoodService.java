package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.channel.Channel;
import me.jiangcai.logistics.entity.ProductType;
import me.jiangcai.logistics.entity.PropertyName;
import me.jiangcai.logistics.entity.PropertyValue;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 主要商品服务
 *
 * @author CJ
 */
public interface MainGoodService {

    /**
     * @param channel 可选的特定渠道
     * @return 在售商品列表及其可售库存
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
    List<MainGood> forSale(Channel channel, ProductType productType, Map<Long, String> propertyValueMap, String... tags);

    /**
     * 暂时用于商城第一个版本
     * 根据特定类型-属性-属性值 查找与此商品相关的其他商品
     *
     * @param channel       可选的特定渠道
     * @param propertyName  商品
     * @param propertyValue 属性键值
     * @return
     */
    @Transactional(readOnly = true)
    MainGood forSaleByPropertyValue(Channel channel, ProductType productType, PropertyName propertyName, PropertyValue propertyValue);

    @Transactional(readOnly = true)
    MainGood findOne(Long goodsId);

    /**
     * 特定渠道中某标签下在上商品列表所用到的属性及属性值
     *
     * @param channel
     * @param tag
     * @return
     */
    Set<String> forSalePropertyValue(Channel channel, String tag);

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
