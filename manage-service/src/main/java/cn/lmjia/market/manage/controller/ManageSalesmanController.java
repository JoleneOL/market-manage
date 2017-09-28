package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.deal.Salesman;
import cn.lmjia.market.core.entity.deal.Salesman_;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.service.ReadService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasRole('ROOT')")
public class ManageSalesmanController {

    @GetMapping("/manageSalesman")
    public String index() {
        return "_salesmanManage.html";
    }

    @GetMapping("/manage/salesmen")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition<Salesman> data(String name) {
        return new RowDefinition<Salesman>() {
            @Override
            public Class<Salesman> entityClass() {
                return Salesman.class;
            }

            @Override
            public List<FieldDefinition<Salesman>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(Salesman.class, "name")
                                .addBiSelect((salesmanRoot, criteriaBuilder) -> ReadService.nameForLogin(salesmanRoot.join(Salesman_.login), criteriaBuilder))
                                .build()
                        , FieldBuilder.asName(Salesman.class, "mobile")
                                .addBiSelect((salesmanRoot, criteriaBuilder) -> ReadService.mobileForLogin(salesmanRoot.join(Salesman_.login), criteriaBuilder))
                                .build()
                        , FieldBuilder.asName(Salesman.class, "enableLabel")
                                .addSelect(salesmanRoot -> salesmanRoot.get(Salesman_.enable))
                                .addFormat((data, type) -> {
                                    boolean enable = (boolean) data;
                                    return enable ? "启用" : "禁用";
                                })
                                .build()
                        , FieldBuilder.asName(Salesman.class, "enableLabel")
                                .addSelect(salesmanRoot -> salesmanRoot.get(Salesman_.enable))
                                .build()
                        , FieldBuilder.asName(Salesman.class, "rateLabel")
                                .addSelect(salesmanRoot -> salesmanRoot.get(Salesman_.salesRate))
                                .addFormat((data, type) -> {
                                    Number number = (Number) data;
                                    return NumberFormat.getPercentInstance(Locale.CHINA).format(number);
                                })
                                .build()
                        , FieldBuilder.asName(Salesman.class, "rate")
                                .addSelect(salesmanRoot -> salesmanRoot.get(Salesman_.salesRate))
                                .addFormat((data, type) -> {
                                    BigDecimal number = (BigDecimal) data;
                                    return number.movePointRight(2).setScale(0, BigDecimal.ROUND_HALF_EVEN);
                                })
                                .build()
                        , Fields.asBasic("rank")
                );
            }

            @Override
            public Specification<Salesman> specification() {
                if (StringUtils.isEmpty(name))
                    return null;
                return (root, query, cb) -> cb.like(ReadService.nameForLogin(root.join(Salesman_.login), cb), "%" + name + "%");
            }
        };
    }

}
