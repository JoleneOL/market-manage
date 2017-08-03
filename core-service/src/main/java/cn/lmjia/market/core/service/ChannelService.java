package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.channel.Channel;
import org.springframework.transaction.annotation.Transactional;

/**
 * 渠道管理
 *
 * @author CJ
 */
public interface ChannelService {
    /**
     * @param name 渠道名称
     * @return null 或者特定的渠道
     */
    @Transactional(readOnly = true)
    Channel findByName(String name);

    /**
     * 保存一个新的分期
     *
     * @param channel 分期
     * @return 新的分期
     */
    @Transactional
    <T extends Channel> T saveChannel(T channel);

    /**
     * 设置这个商品为该渠道所有
     *  @param good    商品
     * @param channel 渠道
     */
    @Transactional
    MainGood setupChannel(MainGood good, Channel channel);

    /**
     * 复制这个商品，并且让新商品为该渠道所有
     *
     * @param good    商品
     * @param channel 渠道
     * @return 新的商品
     */
    @Transactional
    MainGood cloneGoodToChannel(MainGood good, Channel channel);

    @Transactional(readOnly = true)
    Channel get(long id);
}
