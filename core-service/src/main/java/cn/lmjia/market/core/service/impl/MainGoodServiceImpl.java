package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.service.MainGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.List;

/**
 * @author CJ
 */
@Service("mainGoodService")
public class MainGoodServiceImpl implements MainGoodService {
    @Autowired
    private MainGoodRepository mainGoodRepository;

    @Override
    public List<MainGood> forSale(Channel channel) {
        if (channel == null)
            return mainGoodRepository.findAll((root, query, cb) -> {
                Join<MainGood, Channel> channelJoin = root.join("channel", JoinType.LEFT);
                return cb.and(
                        cb.isTrue(root.get("enable"))
                        , cb.or(
                                channelJoin.isNull()
                                , cb.isFalse(channelJoin.get("extra"))
                        )
                );
            });
        return mainGoodRepository.findAll((root, query, cb) -> cb.and(
                cb.isTrue(root.get("enable"))
                , cb.equal(root.get("channel"), channel)
        ));
    }

    @Override
    public List<MainGood> forSale() {
        return forSale(null);
    }
}
