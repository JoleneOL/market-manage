package cn.lmjia.market.core.exception;

import cn.lmjia.market.core.entity.MainGood;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品库存不足提醒，应该让用户知道，是哪个商品库存不够
 * Created by helloztt on 2017/8/23.
 */
@AllArgsConstructor
@Getter
@Setter
public class MainGoodLowStockException extends ServletException {
    @Description("限购商品")
    private final Map<MainGood,Integer> usableGoodStock;



    public Data[] toData(){
        List<Data> dataList = new ArrayList<>();
        usableGoodStock.keySet().forEach(mainGood -> {
            dataList.add(new Data(mainGood.getId(),usableGoodStock.get(mainGood)));
        });
        return dataList.stream().toArray(Data[]::new);
    }

    class Data{
        private Long goodId;
        private Integer stock;

        public Data(Long goodId, Integer stock) {
            this.goodId = goodId;
            this.stock = stock;
        }
    }
}
