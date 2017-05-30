package cn.lmjia.market.core.row;

import org.springframework.http.MediaType;

import java.util.List;
import java.util.function.Function;

/**
 * 也是一种字段，只不过它们的宿主无法支持统计
 *
 * @author CJ
 */
public interface IndefiniteFieldDefinition {

    /**
     * @return 字段名称
     */
    String name();

    /**
     * 导出结果字段
     *
     * @param origin    原数据
     * @param mediaType 意图导出的结果类型
     * @param exportMe  如果origin为List，并且元素类型等同原数据的上级结果类型并且输出结果为重复的，则可以使用该function
     * @return 输出数据
     */
    Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe);
}
