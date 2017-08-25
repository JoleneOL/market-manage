package cn.lmjia.market.core.entity;

import lombok.Data;

import javax.persistence.Embeddable;

/**
 * @author CJ
 */
@Data
@Embeddable
public class OneForElement {
    private String data;
}
