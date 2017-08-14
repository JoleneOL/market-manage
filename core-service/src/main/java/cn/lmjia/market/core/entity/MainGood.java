package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.channel.InstallmentChannel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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

    public static Expression<BigDecimal> getTotalPrice(From<?, MainGood> path, CriteriaBuilder criteriaBuilder) {
        final Path<Object> product = path.get("product");
        Join<MainGood, Channel> channel = path.join("channel", JoinType.LEFT);

        // deposit+install
        final Expression<Number> simpleSum = criteriaBuilder.sum(product.get("deposit"), product.get("install"));
        // 这个情况下 价格等于 deposit*depositRate*(1+poundageRate)+ install
        final Expression<Number> installmentChannelSum = criteriaBuilder.sum(
                criteriaBuilder.prod(
                        criteriaBuilder.prod(product.get("deposit")
                                , channel.get("depositRate"))
                        , criteriaBuilder.sum(criteriaBuilder.treat(channel
                                , InstallmentChannel.class).get("poundageRate")
                                , criteriaBuilder.literal(1))
                )
                , product.get("install")
        );
        // deposit*dRate+ install
        final Expression<Number> otherChannelSum = criteriaBuilder.sum(criteriaBuilder.prod(product.get("deposit")
                , channel.get("depositRate"))
                , product.get("install")
        );
        return criteriaBuilder.toBigDecimal(
                criteriaBuilder.<Boolean, Number>selectCase(channel.isNull())
                        .when(true
                                , simpleSum)
                        .otherwise(
                                criteriaBuilder.<String, Number>selectCase(channel.get("classType").as(String.class))
                                        .when("InstallmentChannel",
                                                installmentChannelSum
                                        )
                                        .otherwise(
                                                otherChannelSum
                                        )
                        )
        );
    }

    /**
     * @return 渠道所带来的溢价或者优惠
     */
    public BigDecimal getChannelChangeAsAdd() {
        return getTotalPrice().subtract(product.getDeposit()).subtract(product.getInstall());
    }

    /**
     * @return 总价
     */
    public BigDecimal getTotalPrice() {
        BigDecimal price = product.getDeposit();
        // 如果是特定渠道的
        if (channel != null) {
            price = price.multiply(channel.getDepositRate());
            if (channel instanceof InstallmentChannel) {
                price = price.add(price.multiply(((InstallmentChannel) channel).getPoundageRate()));
            }
        }
        return price.add(product.getInstall());
    }
}
