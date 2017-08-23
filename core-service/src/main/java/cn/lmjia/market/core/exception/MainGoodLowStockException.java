package cn.lmjia.market.core.exception;

import cn.lmjia.market.core.entity.MainGood;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import javax.servlet.ServletException;

/**
 * 商品库存不足提醒，应该让用户知道，是哪个商品库存不够
 * Created by helloztt on 2017/8/23.
 */
@AllArgsConstructor
@Getter
@Setter
public class MainGoodLowStockException extends ServletException {
    @Description("限购商品")
    private final MainGood mainGood;
}
