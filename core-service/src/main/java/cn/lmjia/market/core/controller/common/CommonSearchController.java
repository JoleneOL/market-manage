package cn.lmjia.market.core.controller.common;

import cn.lmjia.market.core.entity.MainProduct;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.Select2Dramatizer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

/**
 * @author CJ
 */
@Controller
public class CommonSearchController {

    @GetMapping("/product/search")
    @RowCustom(dramatizer = Select2Dramatizer.class, distinct = true)
    public RowDefinition<MainProduct> product(String search) {
        return new RowDefinition<MainProduct>() {
            @Override
            public Class<MainProduct> entityClass() {
                return MainProduct.class;
            }

            @Override
            public List<FieldDefinition<MainProduct>> fields() {
                return Arrays.asList(
                        Fields.asBasic("name")
                        , FieldBuilder.asName(MainProduct.class, "id")
                                .addSelect(mainProductRoot -> mainProductRoot.get("code"))
                                .build()
                );
            }

            @Override
            public Specification<MainProduct> specification() {
                if (StringUtils.isEmpty(search))
                    return null;
                return (root, query, cb) -> {
                    String toSearch = "%" + search + "%";
                    return cb.or(
                            cb.like(root.get("name"), toSearch)
                            , cb.like(root.get("code"), toSearch)
                    );
                };
            }
        };
    }

}
