package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainGood_;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.channel.Channel_;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.util.ApiDramatizer;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.logistics.entity.ProductType_;
import me.jiangcai.logistics.entity.Product_;
import me.jiangcai.logistics.entity.PropertyName_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by helloztt on 2017-09-25.
 */
@Controller
public class WecharSearchController {
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private MainGoodRepository mainGoodRepository;

    @GetMapping("/wechatSearch")
    public String search() {
        return "wechat@mall/search.html";
    }

    @GetMapping("/wechatSearch/tagSearch")
    public String tagSearch(String tagName, Model model) {
        if (!StringUtils.isEmpty(tagName)) {
            model.addAttribute("tag", tagName);
        }
        return "wechat@mall/tagDetail.html";
    }

    @GetMapping("/wechatSearch/goodsDetail/{goodsId}")
    public String goodsDetail(@PathVariable Long goodsId, Model model) {
        model.addAttribute("currentData", mainGoodRepository.findOne(goodsId));
        return "wechat@mall/goodsDetail.html";
    }

    /**
     * @param channel            渠道
     * @param goodsId            商品ID
     * @param productName        货品名称
     * @param propertyTypeId     货品类型
     * @param tag                标签名称
     * @param priceOrder         价格排序
     * @param propertyValue      属性值
     * @param propertyNameValues 属性键值：propertyId:propertyValue|propertyId:propertyValue
     * @return
     */
    @GetMapping("/wechatSearch/goodsList")
    @RowCustom(dramatizer = ApiDramatizer.class, distinct = true)
    public RowDefinition<MainGood> data(final String channel
            , final Long goodsId
            , final String productName
            , final Long propertyTypeId
            , final String tag
            , final String propertyValue
            , final String propertyNameValues
            , final String priceOrder) {
        Map<Long, String> propertyNameValueMap = null;
        if (!StringUtils.isEmpty(propertyNameValues)) {
            for (String propertyNameValue : propertyNameValues.split("\\|")) {
                Long propertyId = Long.valueOf(propertyNameValue.split(":")[0]);
                String property = propertyNameValue.split(":")[1];
                propertyNameValueMap.put(propertyId, property);
            }
        }
        return new RowDefinition<MainGood>() {
            @Override
            public Class<MainGood> entityClass() {
                return MainGood.class;
            }

            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<MainGood> root) {
                if (StringUtils.isEmpty(priceOrder))
                    return Arrays.asList(criteriaBuilder.desc(root.get("createTime")));
                return Arrays.asList("asc".equalsIgnoreCase(priceOrder)
                        ? (criteriaBuilder.asc(MainGood.getTotalPrice(root, criteriaBuilder)))
                        : (criteriaBuilder.desc(MainGood.getTotalPrice(root, criteriaBuilder))));
            }

            @Override
            public List<FieldDefinition<MainGood>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(MainGood.class, "productName")
                                .addSelect(mainGoodRoot -> mainGoodRoot.get("product").get("name"))
                                .build()
                        , FieldBuilder.asName(MainGood.class, "description")
                                .addSelect(mainGoodRoot -> mainGoodRoot.get("product").get("description"))
                                .addFormat((data, type) -> data != null ? data : "")
                                .build()
                        , FieldBuilder.asName(MainGood.class, "price")
                                .addBiSelect(MainGood::getTotalPrice)
                                .build()
                        , FieldBuilder.asName(MainGood.class, "goodsImage")
                                .addSelect(mainGoodRoot -> mainGoodRoot.join("product").get("mainImg"))
                                .addFormat((data, type) -> {
                                    if (data == null) {
                                        return "../../wechat-resource/assets/img/none.png";
                                    }
                                    try {
                                        return resourceService.getResource((String) data).httpUrl().toString();
                                    } catch (IOException e) {
                                        return "../../wechat-resource/assets/img/none.png";
                                    }
                                }).build()
                );
            }

            @Override
            public Specification<MainGood> specification() {
                return (root, query, cb) -> {
                    List<Predicate> predicateList = new ArrayList<>();
                    predicateList.add(cb.isTrue(root.get(MainGood_.enable)));
                    predicateList.add(cb.isTrue(root.get(MainGood_.product).get(Product_.enable)));
                    if (channel == null) {
                        Join<MainGood, Channel> channelJoin = root.join(MainGood_.channel, JoinType.LEFT);
                        predicateList.add(cb.or(
                                channelJoin.isNull()
                                , cb.isFalse(channelJoin.get(Channel_.extra))
                        ));
                    } else {
                        predicateList.add(cb.equal(root.get(MainGood_.channel), channel));
                    }
                    if (goodsId != null && goodsId > 0)
                        predicateList.add(cb.equal(root.get(MainGood_.id), goodsId));
                    if (!StringUtils.isEmpty(productName) && !"all".equalsIgnoreCase(productName))
                        predicateList.add(cb.like(root.get(MainGood_.product).get(Product_.name), "%" + productName + "%"));
                    if (propertyTypeId != null && propertyTypeId > 0)
                        predicateList.add(cb.equal(root.get(MainGood_.product).get(Product_.productType).get(ProductType_.id), propertyTypeId));
                    if (!StringUtils.isEmpty(tag) && !"all".equalsIgnoreCase(tag))
                        predicateList.add(cb.equal(root.get("tags").get("name"), tag));
                    if (!StringUtils.isEmpty(propertyValue) && !"all".equalsIgnoreCase(propertyValue)) {
                        predicateList.add(cb.equal(root.join("product", JoinType.LEFT).joinMap("propertyNameValues").value(), propertyValue));
                    }
                    if (!CollectionUtils.isEmpty(propertyNameValueMap)) {
                        propertyNameValueMap.keySet().forEach(property -> {
                            predicateList.add(cb.equal(root.join(MainGood_.product).joinMap(Product_.propertyNameValues.getName()).key().get(PropertyName_.id.getName()), property));
                            predicateList.add(cb.equal(root.join(MainGood_.product).joinMap(Product_.propertyNameValues.getName()).value(), propertyNameValueMap.get(property)));
                        });
                    }
                    return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
                };
            }
        };
    }


}
