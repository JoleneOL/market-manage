package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.entity.trj.TRJPayOrder;
import cn.lmjia.market.core.service.ReadService;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.field.FieldBuilder;
import me.jiangcai.crud.row.field.Fields;
import me.jiangcai.payment.chanpay.entity.ChanpayPayOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.hua.huabei.entity.HuaHuabeiPayOrder;
import me.jiangcai.payment.paymax.entity.PaymaxPayOrder;

import javax.persistence.criteria.JoinType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 展示订单数据的格式
 *
 * @author CJ
 */
public abstract class MainOrderRows extends AbstractMainDeliverableOrderRows<MainOrder> {

    /**
     * 要渲染这些记录的身份
     */
    private final Login login;

    public MainOrderRows(Login login, Function<LocalDateTime, String> localDateTimeFormatter) {
        super(localDateTimeFormatter);
        this.login = login;
    }

    @Override
    public Class<MainOrder> entityClass() {
        return MainOrder.class;
    }

    @Override
    public List<FieldDefinition<MainOrder>> fields() {
        ArrayList<FieldDefinition<MainOrder>> list = new ArrayList<>();
        list.addAll(super.fields());
        list.add(Fields.asBiFunction("user", ((root, criteriaBuilder)
                -> ReadService.nameForLogin(MainOrder.getOrderByLogin(root)
                , criteriaBuilder))));
        list.add(Fields.asBiFunction("userLevel", ((root, criteriaBuilder)
                -> ReadService.agentLevelForLogin(MainOrder.getOrderByLogin(root)
                , criteriaBuilder))));
        list.add(Fields.asBiFunction("orderId", MainOrder::getSerialId));
        list.add(FieldBuilder.asName(MainOrder.class, "method")
                .addBiSelect(((root, criteriaBuilder) -> root.join("payOrder", JoinType.LEFT)))
                .addFormat((data, type) -> {
                    PayOrder x = (PayOrder) data;
                    if (x == null)
                        return "无";
                    if (x instanceof PaymaxPayOrder)
                        return "拉卡拉";
                    if (x instanceof ChanpayPayOrder)
                        return "畅捷";
                    if (x instanceof TRJPayOrder)
                        return "投融家";
                    if (x instanceof HuaHuabeiPayOrder)
                        return "花呗";
                    return "未知";
                })
                .build());
        list.add(FieldBuilder.asName(MainOrder.class, "methodCode")
                .addBiSelect(((root, criteriaBuilder) -> root.join("payOrder", JoinType.LEFT)))
                .addFormat((data, type) -> {
                    PayOrder x = (PayOrder) data;
                    if (x == null)
                        return 0;
                    if (x instanceof PaymaxPayOrder)
                        return 1;
                    if (x instanceof ChanpayPayOrder)
                        return 4;
                    if (x instanceof TRJPayOrder)
                        return 2;
                    if (x instanceof HuaHuabeiPayOrder)
                        return 3;
                    return 99;
                })
                .build());
        list.add(FieldBuilder.asName(MainOrder.class, "quickDoneAble")
                .addSelect(root -> root.get("orderStatus"))
                .addFormat((data, type) -> {
                    OrderStatus orderStatus = (OrderStatus) data;
                    return login.isManageable() && orderStatus == OrderStatus.forDeliver;
                })
                .build());
        return list;
    }

}
