package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder;
import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder_;
import cn.lmjia.market.core.entity.order.MainDeliverableOrder_;
import cn.lmjia.market.core.service.ReadService;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.field.FieldBuilder;
import me.jiangcai.crud.row.field.Fields;

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
        list.add(Fields.asBiFunction("user", ((root, criteriaBuilder)
                -> ReadService.nameForLogin(root.join(AgentPrepaymentOrder_.belongs)
                , criteriaBuilder))));
        list.add(Fields.asBiFunction("userLevel", ((root, criteriaBuilder)
                -> ReadService.agentLevelForLogin(root.join(AgentPrepaymentOrder_.belongs)
                , criteriaBuilder))));
        list.add(FieldBuilder.asName(AgentPrepaymentOrder.class, "method")
                .addBiSelect((agentPrepaymentOrderRoot, criteriaBuilder) -> criteriaBuilder.literal(1))
                .addFormat((data, type) -> "货款")
                .build());
        list.add(FieldBuilder.asName(AgentPrepaymentOrder.class, "methodCode")
                .addBiSelect((agentPrepaymentOrderRoot, criteriaBuilder) -> criteriaBuilder.literal(99))
                .build());
        list.addAll(super.fields());
        return list;
    }
}
