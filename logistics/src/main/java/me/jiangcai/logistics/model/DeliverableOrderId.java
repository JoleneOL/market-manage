package me.jiangcai.logistics.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.jiangcai.logistics.DeliverableOrder;

import java.io.Serializable;

/**
 * @author CJ
 */
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DeliverableOrderId {
    private Class<? extends DeliverableOrder> type;
    private Serializable id;

    @Override
    public String toString() {
        return type.getName() + ":" + id;
    }
}
