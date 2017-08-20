package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.row.RowDefinition;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * @author CJ
 */
abstract class AbstractRows<T> implements RowDefinition<T> {
    protected final Function<LocalDateTime, String> localDateTimeFormatter;

    AbstractRows(Function<LocalDateTime, String> localDateTimeFormatter) {
        this.localDateTimeFormatter = localDateTimeFormatter;
    }
}
