package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder;
import cn.lmjia.market.core.entity.order.MainDeliverableOrder_;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author CJ
 */
public abstract class AgentPrepaymentOrderRows extends AbstractMainDeliverableOrderRows<AgentPrepaymentOrder> {

    public AgentPrepaymentOrderRows(Function<LocalDateTime, String> localDateTimeFormatter) {
        super(localDateTimeFormatter);
    }

    @Override
    public Class<AgentPrepaymentOrder> entityClass() {
        return AgentPrepaymentOrder.class;
    }

    @Override
    public List<FieldDefinition<AgentPrepaymentOrder>> fields() {
        ArrayList<FieldDefinition<AgentPrepaymentOrder>> list = new ArrayList<>();
        list.add(FieldBuilder.asName(AgentPrepaymentOrder.class, "orderId")
                .addSelect(agentPrepaymentOrderRoot -> agentPrepaymentOrderRoot.get(MainDeliverableOrder_.id))
                .build());
        list.addAll(super.fields());
        return list;
    }
}
