package me.jiangcai.logistics.haier.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.entity.Depot;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class HaierDepot extends Depot {
    /**
     * 海尔（日日顺）仓库编码：按日日顺C码
     */
    @Column(length = 32)
    private String haierCode;

    public HaierDepot() {
        set_type((byte) 1);
    }
}
