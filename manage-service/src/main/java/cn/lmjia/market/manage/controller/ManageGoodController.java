package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.repository.MainProductRepository;
import cn.lmjia.market.core.service.ChannelService;
import cn.lmjia.market.core.service.TagService;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.field.FieldBuilder;
import me.jiangcai.crud.row.field.Fields;
import me.jiangcai.crud.row.supplier.JQueryDataTableDramatizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 商品管理
 *
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_PRODUCT_CENTER + "')")
public class ManageGoodController {

    @Autowired
    private MainGoodRepository mainGoodRepository;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private MainProductRepository mainProductRepository;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private TagService tagService;

    // 禁用和恢复
    @PutMapping("/goods/{id}/off")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void disable(@PathVariable("id") long id) {
        mainGoodRepository.getOne(id).setEnable(false);
    }

    @PutMapping("/goods/{id}/on")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void enable(@PathVariable("id") long id) {
        mainGoodRepository.getOne(id).setEnable(true);
    }

    @PreAuthorize("hasAnyRole('ROOT','"+ Login.ROLE_SUPPLY_CHAIN+"','"+Login.ROLE_LOOK+"')")
    @GetMapping("/manageGood")
    public String index() {
        return "_goodsManage.html";
    }

    @GetMapping("/manageGoodAdd")
    public String indexForCreate() {
        return "_goodsOperate.html";
    }

    @GetMapping("/manageGoodEdit")
    public String indexForEdit(long id, Model model) {
        model.addAttribute("currentData", mainGoodRepository.getOne(id));
        return "_goodsOperate.html";
    }

    @PostMapping("/manageGoodSubmit")
    @Transactional
    public String edit(Long id, boolean commissionSource, String product, Long channel, String[] tag) {
        MainGood good;
        if (id != null)
            good = mainGoodRepository.getOne(id);
        else {
            good = new MainGood();
            good.setCreateTime(LocalDateTime.now());
        }

        if (good.getProduct() == null && StringUtils.isEmpty(product))
            throw new IllegalArgumentException();

        if (good.getProduct() == null) {
            good.setProduct(mainProductRepository.getOne(product));
        }

        if (channel != null) {
            if (channel > 0)
                good.setChannel(channelService.get(channel));
            else
                good.setChannel(null);
        }
        good.setCommissionSource(commissionSource);

        Set<Tag> tags = null;
        if (tag != null && tag.length > 0) {
            tags = new HashSet<>();
            for (String tagName : tag) {
                tags.add(tagService.save(tagName));
            }
        }
        good.setTags(tags);

        if (id == null)
            mainGoodRepository.save(good);

        return "redirect:/manageGood";
    }

    @PreAuthorize("hasAnyRole('ROOT','"+ Login.ROLE_SUPPLY_CHAIN+"','"+Login.ROLE_LOOK+"')")
    @GetMapping("/goods/list")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition<MainGood> data(final String productName) {
        return new RowDefinition<MainGood>() {
            @Override
            public Class<MainGood> entityClass() {
                return MainGood.class;
            }

            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<MainGood> root) {
                return Arrays.asList(
                        criteriaBuilder.asc(root.get("enable"))
                        , criteriaBuilder.desc(root.get("createTime"))
                );
            }

            @Override
            public List<FieldDefinition<MainGood>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(MainGood.class, "productName")
                                .addSelect(mainGoodRoot -> mainGoodRoot.get("product").get("name"))
                                .build()
                        , Fields.asBasic("enable")
                        , FieldBuilder.asName(MainGood.class, "channelName")
                                .addSelect(mainGoodRoot -> mainGoodRoot.join("channel", JoinType.LEFT).get("name"))
                                .build()
                        , FieldBuilder.asName(MainGood.class, "createTime")
                                .addFormat((data, type) -> conversionService.convert(data, String.class))
                                .build()
                );
            }

            @Override
            public Specification<MainGood> specification() {
                return (root, query, cb) -> {
                    Predicate predicate = cb.conjunction();
                    if (!StringUtils.isEmpty(productName))
                        predicate = cb.and(cb.like(root.get("product").get("name"), "%" + productName + "%"));
                    return predicate;
                };
            }
        };
    }

}
