package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.logistics.PersistingReadable;
import me.jiangcai.logistics.entity.support.ShiftStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.LocalDateTime;

/**
 * 导致状态改变的事件，通常带着说明
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public class StockShiftUnitEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime time;
    /**
     * 说明
     */
    @Column(length = 100)
    private String message;
    private ShiftStatus toStatus;
    private PersistingReadable source;

}
