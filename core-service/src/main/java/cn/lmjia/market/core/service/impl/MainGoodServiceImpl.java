package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainGood_;
import cn.lmjia.market.core.entity.Tag_;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.channel.Channel_;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.service.MainGoodService;
import me.jiangcai.logistics.entity.Product_;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CJ
 */
@Service("mainGoodService")
public class MainGoodServiceImpl implements MainGoodService {
    private static final Log log = LogFactory.getLog(MainGoodServiceImpl.class);
    @Autowired
    private MainGoodRepository mainGoodRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<MainGood> forSale(Channel channel) {
        return forSale(channel,null);
    }

    @Override
    public List<MainGood> forSale(Channel channel, String... tags) {
        return mainGoodRepository.findAll((root, query, cb) -> {
            Join<MainGood, Channel> channelJoin = root.join(MainGood_.channel, JoinType.LEFT);
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(cb.isTrue(root.get(MainGood_.enable)));
            predicateList.add(cb.isTrue(root.get(MainGood_.product).get(Product_.enable)));
            if(channel == null){
                predicateList.add(cb.or(
                        channelJoin.isNull()
                        , cb.isFalse(channelJoin.get(Channel_.extra))
                ));
            }else{
                predicateList.add(cb.equal(root.get(MainGood_.channel), channel));
            }
            if(tags != null && tags.length > 0){
                List<Predicate> tagSearchPredicateList = new ArrayList<>();
                for (String tag : tags) {
                    tagSearchPredicateList.add(cb.equal(root.join(MainGood_.tags).get(Tag_.name), tag));
                }
                predicateList.add(cb.or(tagSearchPredicateList.toArray(new Predicate[tagSearchPredicateList.size()])));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }

    @Override
    public List<MainGood> forSale() {
        return forSale(null,null);
    }

    @Override
    public void priceCheck() {
        mainGoodRepository.findAll().forEach(good -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<BigDecimal> priceQuery = cb.createQuery(BigDecimal.class);
            Root<MainGood> root = priceQuery.from(MainGood.class);

            BigDecimal value = entityManager.createQuery(priceQuery.select(MainGood.getTotalPrice(root, cb))
                    .where(cb.equal(root, good)))
                    .getSingleResult();
            assert value.equals(good.getTotalPrice());
        });
    }
}
