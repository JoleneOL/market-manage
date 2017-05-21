package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.service.ReadService;

import java.util.Arrays;
import java.util.List;

/**
 * 展示订单数据的格式
 *
 * @author CJ
 */
public abstract class MainOrderRows implements RowDefinition<MainOrder> {

    @Override
    public Class<MainOrder> entityClass() {
        return MainOrder.class;
    }

    @Override
    public List<FieldDefinition<MainOrder>> fields() {
        return Arrays.asList(
                Fields.asBasic("id")
                , Fields.asBiFunction("orderId", MainOrder::getSerialId)
                , Fields.asBiFunction("orderUser", ((root, criteriaBuilder)
                        -> ReadService.nameForLogin(MainOrder.getLogin(root)
                        , criteriaBuilder)))
                , Fields.asBiFunction("phone", (root, criteriaBuilder)
                        -> ReadService.mobileForLogin(MainOrder.getLogin(root), criteriaBuilder))
                , Fields.asFunction("category", root -> root.get("good").get("product").get("name"))
                , Fields.asFunction("type", root -> root.get("good").get("product").get("code"))
                , Fields.asBasic("amount")
                , Fields.asBiFunction("package", ((root, criteriaBuilder)
                        -> criteriaBuilder.literal("")))
                , Fields.asBiFunction("method", ((root, criteriaBuilder)
                        -> criteriaBuilder.literal("")))
        );
    }
}
