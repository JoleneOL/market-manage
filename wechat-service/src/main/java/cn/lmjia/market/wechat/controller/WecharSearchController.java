package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainGood_;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.channel.Channel_;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.logistics.entity.Product_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by helloztt on 2017-09-25.
 */
@Controller
public class WecharSearchController {
    @Autowired
    private ResourceService resourceService;

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

    @GetMapping("/wechatSearch/goodsList")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition<MainGood> data(final String channel
            , final String productName
            , final String tag
            , final String priceOrder
            , final String propertyValue) {
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
//                        , FieldBuilder.asName(MainGood.class, "tags")
//                                .addBiSelect((mainGoodRoot, criteriaBuilder) -> mainGoodRoot.join("tags", JoinType.LEFT).get("name"))
//                                .build()
                        , FieldBuilder.asName(MainGood.class, "description")
                                .addSelect(mainGoodRoot -> mainGoodRoot.get("product").get("description"))
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
                    if (!StringUtils.isEmpty(productName) && !"all".equalsIgnoreCase(productName))
                        predicateList.add(cb.like(root.get("product").get("name"), "%" + productName + "%"));
                    if (!StringUtils.isEmpty(tag) && !"all".equalsIgnoreCase(tag))
                        predicateList.add(cb.equal(root.get("tags").get("name"), tag));
                    if (!StringUtils.isEmpty(propertyValue) && !"all".equalsIgnoreCase(propertyValue)) {
                        predicateList.add(cb.equal(root.join("product", JoinType.LEFT).joinMap("propertyNameValues").value(), propertyValue));
                    }
                    return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
                };
            }
        };
    }
}
