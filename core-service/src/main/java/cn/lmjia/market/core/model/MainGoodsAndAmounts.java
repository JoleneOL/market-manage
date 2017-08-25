package cn.lmjia.market.core.model;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.repository.MainGoodRepository;
import lombok.Data;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 包含一组商品id以及数量的集合
 *
 * @author CJ
 */
public class MainGoodsAndAmounts extends ArrayList<MainGoodsAndAmounts.MainGoodAndAmount> {

    public static MainGoodsAndAmounts ofArray(String[] originStrings) {
        String[] strings;
        if (originStrings.length == 2 && !originStrings[0].contains(","))
            strings = new String[]{originStrings[0] + "," + originStrings[1]};
        else
            strings = originStrings;
        MainGoodsAndAmounts amounts = new MainGoodsAndAmounts();
        Stream.of(strings)
                .map(s -> {
                    if (StringUtils.isEmpty(s))
                        return null;
                    String[] data = s.split(",");
                    return new MainGoodAndAmount(NumberUtils.parseNumber(data[0], Long.class)
                            , NumberUtils.parseNumber(data[1], Integer.class));
                })
                .filter(Objects::nonNull)
                .forEach(amounts::add);
        return amounts;
    }

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
