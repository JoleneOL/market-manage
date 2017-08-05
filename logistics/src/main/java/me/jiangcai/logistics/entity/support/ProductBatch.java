package me.jiangcai.logistics.entity.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * 产品批次
 *
 * @author CJ
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductBatch {

    private ProductStatus productStatus;
    private int amount;

}
