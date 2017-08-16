package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.logistics.entity.UsageStock;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 手动发货控制器
 *
 * @author CJ
 */
@Controller
@PreAuthorize("hasRole('ROOT')")
public class ManageManualOrderController {

    @GetMapping("/storage/transfer")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition<UsageStock> useAbleStock(String product, int amount) {
        return new RowDefinition<UsageStock>() {
            @Override
            public Class<UsageStock> entityClass() {
                return UsageStock.class;
            }

            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<UsageStock> root) {
                return Collections.singletonList(criteriaBuilder.desc(root.get("amount")));
            }

            @Override
            public List<FieldDefinition<UsageStock>> fields() {
                return Arrays.asList(
                        FieldBuilder.asName(UsageStock.class, "id")
                                .addSelect(usageStockRoot -> usageStockRoot.get("depot").get("id"))
                                .build()
                        , FieldBuilder.asName(UsageStock.class, "storage")
                                .addSelect(usageStockRoot -> usageStockRoot.get("depot").get("name"))
                                .build()
                        , FieldBuilder.asName(UsageStock.class, "quantity")
                                .addSelect(usageStockRoot -> usageStockRoot.get("amount"))
                                .build()
                );
            }

            @Override
            public Specification<UsageStock> specification() {
                return (root, query, cb) -> cb.and(
                        cb.equal(root.get("product").get("code"), product)
                        , cb.greaterThanOrEqualTo(root.get("amount"), amount)
                );
            }
        };
    }

}
