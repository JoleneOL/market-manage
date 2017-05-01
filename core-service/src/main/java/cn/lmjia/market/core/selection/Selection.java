package cn.lmjia.market.core.selection;

/**
 * @author CJ
 */
public interface Selection<T> {
    /**
     * @return 展现的名字
     */
    String getName();

    /**
     * @param data 实际数据
     * @return 以 {@link #getName()} 展现的数据；如果{@link #supportIterable()}为true则必须返回{@link Iterable}
     */
    Object selectData(T data);

    /**
     * @return 这项数据是否支持内联List
     */
    default boolean supportIterable() {
        return false;
    }

}
