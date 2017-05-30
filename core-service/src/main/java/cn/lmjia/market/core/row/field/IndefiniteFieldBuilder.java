package cn.lmjia.market.core.row.field;

import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.IndefiniteFieldDefinition;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 字段构建器
 *
 * @author CJ
 */
public class IndefiniteFieldBuilder {

    private final String name;
    private BiFunction<Object, MediaType, Object> format;

    private IndefiniteFieldBuilder(String name) {
        this.name = name;
    }

    /**
     * 开始一个构造器
     *
     * @param name {@link FieldDefinition#name()}
     * @return 新的构造器
     */
    public static IndefiniteFieldBuilder asName(String name) {
        return new IndefiniteFieldBuilder(name);
    }

    public IndefiniteFieldBuilder addFormat(BiFunction<Object, MediaType, Object> format) {
        this.format = format;
        return this;
    }

    public IndefiniteFieldDefinition build() {
        return new IndefiniteFieldDefinition() {

            @Override
            public String name() {
                return name;
            }

            @Override
            public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
                if (format != null)
                    return format.apply(origin, mediaType);
                return origin;
            }
        };
    }


}
