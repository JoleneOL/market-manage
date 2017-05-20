package cn.lmjia.market.core.entity.financing;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * 代理商有关的收入
 *
 * @author CJ
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Setter
@Getter
public class AgentIncomeRecord extends IncomeRecord {

}
