package cn.lmjia.market.core.entity.support;

import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by helloztt on 2017/9/16.
 */
@Converter
public class GoodTagConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        if (StringUtils.isEmpty(attribute))
            return null;
        String tags = attribute.stream().collect(Collectors.joining("|"));
        if (!StringUtils.isEmpty(tags))
            tags = "|" + tags + "|";
        return tags;
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        if (StringUtils.isEmpty(dbData))
            return null;
        Set<String> tags = new HashSet<>(Arrays.asList(dbData.split("\\|")));
        return tags.stream().filter(tag -> !StringUtils.isEmpty(tag)).collect(Collectors.toSet());
    }
}
