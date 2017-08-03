package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import java.math.BigDecimal;

/**
 * 具体的商品，用以销售
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class MainGood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 上架状态
     */
    private boolean enable;
    @ManyToOne
    private MainProduct product;
    @ManyToOne
    private Channel channel;

    public static Expression<BigDecimal> getTotalPrice(Path<MainGood> path, CriteriaBuilder criteriaBuilder) {
        final Path<Object> product = path.get("product");
        return criteriaBuilder.toBigDecimal(
                criteriaBuilder.sum(product.get("deposit"), product.get("install"))
        );
    }

    /**
     * @return 总价
     */
    public BigDecimal getTotalPrice() {
        return product.getDeposit().add(product.getInstall());
    }
}
