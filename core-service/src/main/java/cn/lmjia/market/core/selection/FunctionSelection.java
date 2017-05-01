package cn.lmjia.market.core.selection;

import java.util.function.Function;

/**
 * @author CJ
 */
public class FunctionSelection<T> implements Selection<T> {

    private final String name;
    private final Function<T, ?> function;

    public FunctionSelection(String name, Function<T, ?> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object selectData(T data) {
        return function.apply(data);
    }
}
