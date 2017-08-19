package cn.lmjia.market.core.model;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.repository.MainGoodRepository;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 包含一组商品id以及数量的集合
 *
 * @author CJ
 */
public class MainGoodsAndAmounts extends ArrayList<MainGoodsAndAmounts.MainGoodAndAmount> {

    public Map<MainGood, Integer> toReal(MainGoodRepository mainGoodRepository) {
        Map<Long, Integer> map = new HashMap<>();
        forEach(mainGoodAndAmount -> {
            if (map.computeIfPresent(mainGoodAndAmount.getGoodId(), (aLong, integer) -> map.get(aLong) + integer) == null)
                map.computeIfAbsent(mainGoodAndAmount.getGoodId(), aLong -> mainGoodAndAmount.getAmount());
        });
        return map.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(aLong -> mainGoodRepository.getOne(aLong.getKey())
                        , Map.Entry::getValue));
    }

    @Data
    public static class MainGoodAndAmount {
        private final long goodId;
        private final int amount;
    }
}
