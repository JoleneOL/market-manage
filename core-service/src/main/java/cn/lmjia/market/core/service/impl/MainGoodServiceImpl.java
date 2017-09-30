package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainGood_;
import cn.lmjia.market.core.entity.Tag_;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.channel.Channel_;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.service.MainGoodService;
import cn.lmjia.market.core.service.MainOrderService;
import me.jiangcai.logistics.entity.ProductType;
import me.jiangcai.logistics.entity.ProductType_;
import me.jiangcai.logistics.entity.Product_;
import me.jiangcai.logistics.entity.PropertyName;
import me.jiangcai.logistics.entity.PropertyName_;
import me.jiangcai.logistics.entity.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author CJ
 */
@Service("mainGoodService")
public class MainGoodServiceImpl implements MainGoodService {
    @Autowired
    private MainGoodRepository mainGoodRepository;
    @Autowired
    private MainOrderService mainOrderService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<MainGood> forSale(Channel channel) {
        return forSale(channel, null, null, null);
    }

    @Override
    public List<MainGood> forSale(Channel channel, ProductType productType, Map<Long, String> propertyValueMap, String... tags) {
        List<MainGood> mainGoodList = mainGoodRepository.findAll((root, query, cb) -> {
            Join<MainGood, Channel> channelJoin = root.join(MainGood_.channel, JoinType.LEFT);
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(cb.isTrue(root.get(MainGood_.enable)));
            predicateList.add(cb.isTrue(root.get(MainGood_.product).get(Product_.enable)));
            if (channel == null) {
                predicateList.add(cb.or(
                        channelJoin.isNull()
                        , cb.isFalse(channelJoin.get(Channel_.extra))
                ));
            } else {
                predicateList.add(cb.equal(root.get(MainGood_.channel), channel));
            }
            if (productType != null) {
                predicateList.add(cb.equal(root.get(MainGood_.product).get(Product_.productType).get(ProductType_.id), productType.getId()));
            }
            if (!CollectionUtils.isEmpty(propertyValueMap)) {
                propertyValueMap.keySet().forEach(property -> {
                    predicateList.add(cb.equal(root.join(MainGood_.product).joinMap(Product_.propertyNameValues.getName()).key().get(PropertyName_.id.getName()), property));
                    predicateList.add(cb.equal(root.join(MainGood_.product).joinMap(Product_.propertyNameValues.getName()).value(), propertyValueMap.get(property)));
                });
            }
            if (tags != null && tags.length > 0) {
                List<Predicate> tagSearchPredicateList = new ArrayList<>();
                for (String tag : tags) {
                    if (!StringUtils.isEmpty(tag))
                        tagSearchPredicateList.add(cb.equal(root.join(MainGood_.tags).get(Tag_.name), tag));
                }
                if (tagSearchPredicateList.size() > 0)
                    predicateList.add(cb.or(tagSearchPredicateList.toArray(new Predicate[tagSearchPredicateList.size()])));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
        if (mainGoodList != null && mainGoodList.size() > 0) {
            mainOrderService.calculateGoodStock(mainGoodList);
        }
        return mainGoodList;
    }

    @Override
    public MainGood forSaleByPropertyValue(Channel channel, ProductType productType, PropertyName propertyName, PropertyValue propertyValue) {
        Map<Long, String> propertyNameValueMap = new HashMap<>();
        propertyNameValueMap.put(propertyName.getId(), propertyValue.getValue());
        List<MainGood> goodList = forSale(channel, productType, propertyNameValueMap, null);
        return CollectionUtils.isEmpty(goodList) ? null : goodList.get(0);
    }

    @Override
    public MainGood findOne(Long goodsId) {
        MainGood mainGood = mainGoodRepository.findOne(goodsId);
        if(mainGood != null){
            mainOrderService.calculateGoodStock(Arrays.asList(mainGood));
        }
        return mainGood;
    }

    @Override
    public Set<String> forSalePropertyValue(Channel channel, String tag) {
        List<MainGood> forSaleList = forSale(channel, null, null, tag);
        if (forSaleList != null) {
            Set<String> propertyValues = new HashSet<>();
            forSaleList.forEach(mainGood -> mainGood.getProduct().getSpecPropertyNameValues().values().forEach(value -> {
                if (!propertyValues.contains(value))
                    propertyValues.add(value);
            }));
            return propertyValues;
        }
        return null;
    }

    @Override
    public List<MainGood> forSale() {
        return forSale(null, null, null, null);
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
