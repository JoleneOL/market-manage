package cn.lmjia.market.core.entity.financing;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * 财务支出记录
 *
 * @author CJ
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Setter
@Getter
public class OutgoRecord extends FinancingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
