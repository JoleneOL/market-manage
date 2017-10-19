package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class SubEntity extends SuperEntity {

    private String p2;
}
