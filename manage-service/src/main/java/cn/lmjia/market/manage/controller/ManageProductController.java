package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainProduct;
import cn.lmjia.market.core.repository.MainProductRepository;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.service.MainOrderService;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.seext.FileUtils;
import me.jiangcai.logistics.entity.Product_;
import me.jiangcai.logistics.entity.PropertyName;
import me.jiangcai.logistics.haier.HaierSupplier;
import me.jiangcai.logistics.repository.ProductTypeRepository;
import me.jiangcai.logistics.repository.PropertyNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 货品管理
 * 增加推送至日日顺的功能
 * 如果资料未全则不让推送
 * 允许编辑但是跟推送相关的资源 一旦存在值就不可编辑
 *
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_PRODUCT_CENTER + "')")
public class ManageProductController {

    @Autowired
    private MainProductRepository mainProductRepository;
    @Autowired
    private HaierSupplier haierSupplier;
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private PropertyNameRepository propertyNameRepository;
    @Autowired
    private ResourceService resourceService;

    // 禁用和恢复
    @PostMapping("/products")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void disable(String code) {
        mainProductRepository.getOne(code).setEnable(false);
    }

    @DeleteMapping("/products")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(String code) {
        final MainProduct one = mainProductRepository.getOne(code);
        one.setEnable(false);
        one.setDeleted(true);
    }

    @PutMapping("/products")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void enable(String code) {
        mainProductRepository.getOne(code).setEnable(true);
    }

    // 推送
    @PutMapping("/productsHaier")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pushHaier(String code) {
        MainProduct product = mainProductRepository.getOne(code);
        // 信息校验下先
        if (StringUtils.isEmpty(product.getBrand()))
            throw new IllegalArgumentException("");
        if (StringUtils.isEmpty(product.getUnit()))
            throw new IllegalArgumentException("");
        if (StringUtils.isEmpty(product.getSKU()))
            throw new IllegalArgumentException("");
        if (StringUtils.isEmpty(product.getMainCategory()))
            throw new IllegalArgumentException("");
        if (product.getVolumeHeight() == null)
            throw new IllegalArgumentException("");
        if (product.getVolumeHeight() == null)
            throw new IllegalArgumentException("");
        if (product.getVolumeWidth() == null)
            throw new IllegalArgumentException("");
        if (product.getWeight() == null)
            throw new IllegalArgumentException("");
        haierSupplier.updateProduct(product);
    }
    @PreAuthorize("hasAnyRole('ROOT','"+ Login.ROLE_SUPPLY_CHAIN+"','"+Login.ROLE_LOOK+"')")
    @GetMapping("/manageProduct")
    public String index() {
        return "_productManage.html";
    }

    @GetMapping("/manageProductAdd")
    public String indexForCreate(@RequestParam Long productTypeId, Model model) {
        model.addAttribute("productType", productTypeRepository.findOne(productTypeId));
        return "_productOperate.html";
    }

    @GetMapping("/manageProductEdit")
    public String indexForEdit(String code, Model model) {
        model.addAttribute("currentData", mainProductRepository.getOne(code));
        return "_productOperate.html";
    }

    @GetMapping("/manageProductDetail")
    public String detail(String code, Model model) {
        model.addAttribute("currentData", mainProductRepository.getOne(code));
        return "_productDetail.html";
    }

    @PostMapping("/manageProductSubmit")
    @Transactional
    public String edit(boolean createNew, String productName, String productBrand, String mainCategory
            , @RequestParam("type") String code, String SKU, BigDecimal productPrice, String unit, BigDecimal length
            , BigDecimal width, BigDecimal height, BigDecimal weight, BigDecimal serviceCharge, String productSummary
            , String productDetail, boolean installation
            , Long productType, String propertyNameValue, String productImgPath
            , @RequestParam(required = false, defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate planSellOutDate) throws IOException {
        MainProduct product;
        if (createNew) {
            if (StringUtils.isEmpty(code))
                throw new IllegalArgumentException("");
            if (StringUtils.isEmpty(productName))
                throw new IllegalArgumentException("");
            if (mainProductRepository.findOne(code) != null)
                throw new IllegalArgumentException("");
            if (productTypeRepository.findOne(productType) == null)
                throw new IllegalArgumentException("");
            product = new MainProduct();
            product.setProductType(productTypeRepository.findOne(productType));
            product.setCode(code);
        } else {
            product = mainProductRepository.getOne(code);
        }

        product.setName(productName);
        product.setInstallation(installation);
        product.setBrand(StringUtils.isEmpty(productBrand) ? null : productBrand);
        product.setMainCategory(StringUtils.isEmpty(mainCategory) ? null : mainCategory);
        product.setSKU(StringUtils.isEmpty(SKU) ? null : SKU);
        product.setDeposit(productPrice);
        product.setUnit(StringUtils.isEmpty(unit) ? null : unit);
        product.setVolumeLength(length);
        product.setVolumeWidth(width);
        product.setVolumeHeight(height);
        product.setWeight(weight);
        product.setInstall(serviceCharge);
        //如果计划售罄时间有改动，就要重新计算今日限购数量
        boolean isCleanProductStock = (planSellOutDate != null && !planSellOutDate.equals(product.getPlanSellOutDate()))
                || (product.getPlanSellOutDate() != null && !product.getPlanSellOutDate().equals(planSellOutDate));
        product.setPlanSellOutDate(planSellOutDate);
        product.setDescription(StringUtils.isEmpty(productSummary) ? null : productSummary);
        product.setRichDescription(StringUtils.isEmpty(productDetail) ? null : productDetail);
        // 设置货品规格及规格值
        Map<PropertyName, String> propertyNameValues = null;
        if (!StringUtils.isEmpty(propertyNameValue)) {
            propertyNameValues = new HashMap<>();
            for (String nameValue : propertyNameValue.split(",")) {
                PropertyName propertyName = propertyNameRepository.findOne(Long.valueOf(nameValue.split(":")[0]));
                String propertyValue = nameValue.split(":")[1];
                propertyNameValues.put(propertyName, propertyValue);
            }
        }
        product.setPropertyNameValues(propertyNameValues);
        product = mainProductRepository.saveAndFlush(product);

        mainProductRepository.save(product);
        if(isCleanProductStock){
            mainOrderService.cleanProductStock(product);
        }
        //转存资源
        if (!StringUtils.isEmpty(productImgPath) && productImgPath.length() > 1) {
            String productImgResource = "product/" + product.getCode() + "." + FileUtils.fileExtensionName(productImgPath);
            resourceService.moveResource(productImgResource, productImgPath);
            product.setMainImg(productImgResource);
        }
        return "redirect:/manageProduct";
    }

    @GetMapping("/products/list")
    @PreAuthorize("hasAnyRole('ROOT','"+ Login.ROLE_SUPPLY_CHAIN+"','"+Login.ROLE_LOOK+"')")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition<MainProduct> data(final String productName, final String type) {
        return new RowDefinition<MainProduct>() {
            @Override
            public Class<MainProduct> entityClass() {
                return MainProduct.class;
            }

            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<MainProduct> root) {
                return Arrays.asList(
                        criteriaBuilder.asc(root.get("enable"))
                        , criteriaBuilder.desc(root.get("createTime"))
                );
            }

            @Override
            public List<FieldDefinition<MainProduct>> fields() {
                return Arrays.asList(
                        Fields.asBasic("code")
                        , Fields.asBasic("brand")
                        , FieldBuilder.asName(MainProduct.class, "productName")
                                .addSelect(mainProductRoot -> mainProductRoot.get("name"))
                                .build()
                        , FieldBuilder.asName(MainProduct.class, "category")
                                .addSelect(mainProductRoot -> mainProductRoot.get("mainCategory"))
                                .build()
                        , Fields.asBasic("enable")
                        , FieldBuilder.asName(MainProduct.class, "price")
                                .addSelect(mainProductRoot -> mainProductRoot.get("deposit"))
                                .build()
                        , FieldBuilder.asName(MainProduct.class, "installFee")
                                .addSelect(mainProductRoot -> mainProductRoot.get("install"))
                                .build()

                );
            }

            @Override
            public Specification<MainProduct> specification() {
                return (root, query, cb) -> {
                    Predicate predicate = cb.isFalse(root.get(Product_.deleted));
                    if (!StringUtils.isEmpty(productName))
                        predicate = cb.and(cb.like(root.get("name"), "%" + productName + "%"));
                    if (!StringUtils.isEmpty(type))
                        predicate = cb.and(cb.like(root.get("code"), "%" + type + "%"));
                    return predicate;
                };
            }
        };
    }

}
