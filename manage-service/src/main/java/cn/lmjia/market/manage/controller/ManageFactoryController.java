package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Factory;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.FactoryRepository;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.jpa.entity.support.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 工厂管理,具有Root权限和供应链管理可以操作
 *
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','"+ Login.ROLE_SUPPLY_CHAIN+"')")
public class ManageFactoryController {

    @Autowired
    private FactoryRepository factoryRepository;
    @Autowired
    private ConversionService conversionService;

    @PreAuthorize("hasAnyRole('ROOT','"+ Login.ROLE_SUPPLY_CHAIN+"','"+Login.ROLE_LOOK+"')")
    @GetMapping("/manageFactory")
    public String index() {
        return "_factoryManage.html";
    }

    @GetMapping("/manageFactoryAdd")
    public String toAdd() {
        return "_factoryAdd.html";
    }

    @GetMapping("/manage/factoryList")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition data() {
        return new RowDefinition<Factory>() {

            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<Factory> root) {
                return Arrays.asList(
                        criteriaBuilder.asc(root.get("enable"))
                        , criteriaBuilder.desc(root.get("createTime"))
                );
            }

            @Override
            public Class<Factory> entityClass() {
                return Factory.class;
            }

            @Override
            public List<FieldDefinition<Factory>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(Factory.class, "address")
                                .addSelect(root -> root.get("address"))
                                .addFormat((object, type) -> object.toString())
                                .build()
                        , Fields.asBasic("name")
                        , Fields.asBasic("chargePeopleName")
                        , Fields.asBasic("chargePeopleMobile")
                        , FieldBuilder.asName(Factory.class, "createTime")
                                .addFormat((data, type)
                                        -> conversionService.convert(data, String.class))
                                .build()
                        , Fields.asBasic("enable")
                );
            }

            @Override
            public Specification<Factory> specification() {
                return null;
            }
        };
    }

    @PostMapping("/manage/factoryList")
    public String add(String name, Address address, String chargePeopleName
            , String chargePeopleMobile) {
        Factory factory = new Factory();
        factory.setEnable(true);
        factory.setCreateTime(LocalDateTime.now());
        factory.setName(name);
        factory.setAddress(address);
        factory.setChargePeopleName(chargePeopleName);
        factory.setChargePeopleMobile(chargePeopleMobile);
        factoryRepository.save(factory);
        return "redirect:/manageFactory";
    }

    @PutMapping("/manage/factoryList/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void disable(@PathVariable("id") long id) {
        factoryRepository.getOne(id).setEnable(false);
    }

    @PutMapping("/manage/factoryList/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void enable(@PathVariable("id") long id) {
        factoryRepository.getOne(id).setEnable(true);
    }

}
