package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.repository.channel.ChannelRepository;
import cn.lmjia.market.core.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private MainGoodRepository mainGoodRepository;

    @Override
    public Channel findByName(String name) {
        return channelRepository.findByName(name);
    }

    @Override
    public Channel saveChannel(Channel channel) {
        return channelRepository.save(channel);
    }

    @Override
    public void setupChannel(MainGood good, Channel channel) {
        good.setChannel(channel);
        mainGoodRepository.save(good);
    }

    @Override
    public MainGood cloneGoodToChannel(MainGood good, Channel channel) {
        MainGood newGood = new MainGood();
        newGood.setChannel(channel);
        newGood.setEnable(good.isEnable());
        newGood.setProduct(good.getProduct());
        return mainGoodRepository.save(newGood);
    }
}
