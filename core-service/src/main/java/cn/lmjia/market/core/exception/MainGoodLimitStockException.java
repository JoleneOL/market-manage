package cn.lmjia.market.core.exception;

import cn.lmjia.market.core.entity.MainGood;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 商品限购，这也算一种 [ 库存不足 ] 的异常，还应该让客户知道，什么时候解除限制
 * Created by helloztt on 2017/8/23.
 */
@Getter
@Setter
public class MainGoodLimitStockException extends MainGoodLowStockException {

    @Description("限购解除时间")
    private final Map<MainGood,LocalDateTime> relieveTime;

    public MainGoodLimitStockException(Map<MainGood,Integer> goodStock,Map<MainGood,LocalDateTime> relieveTime) {
        super(goodStock);
        this.relieveTime = relieveTime;
    }
}
