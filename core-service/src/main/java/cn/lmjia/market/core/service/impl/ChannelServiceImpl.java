package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.repository.channel.ChannelRepository;
import cn.lmjia.market.core.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

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
    public <T extends Channel> T saveChannel(T channel) {
        return channelRepository.save(channel);
    }

    @Override
    public MainGood setupChannel(MainGood good, Channel channel) {
        good.setChannel(channel);
        if (channel.getMainGoodSet() == null) {
            channel.setMainGoodSet(new HashSet<>());
        }

        final MainGood save = mainGoodRepository.save(good);
        channel.getMainGoodSet().add(save);
        channelRepository.save(channel);
        return save;
    }

    @Override
    public MainGood cloneGoodToChannel(MainGood good, Channel channel) {
        MainGood newGood = new MainGood();
        newGood.setEnable(good.isEnable());
        newGood.setProduct(good.getProduct());
        newGood.setChannel(channel);
        return setupChannel(newGood, channel);
    }

    @Override
    public Channel get(long id) {
        return channelRepository.getOne(id);
    }
}
