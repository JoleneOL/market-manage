package cn.lmjia.market.core.converter;

import cn.lmjia.market.core.entity.support.OrderStatus;
import me.jiangcai.wx.model.Gender;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CJ
 */
public class EnumConverterFactory implements ConverterFactory<String, Enum> {

    private final Map<Class, Converter> converterMap = new HashMap<>();

    {
        converterMap.put(Gender.class, new GenderConverter());
        converterMap.put(OrderStatus.class, new OrderStatusConverter());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return converterMap.get(targetType);
    }
}
