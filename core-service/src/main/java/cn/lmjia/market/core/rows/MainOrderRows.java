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

        );
    }
}
