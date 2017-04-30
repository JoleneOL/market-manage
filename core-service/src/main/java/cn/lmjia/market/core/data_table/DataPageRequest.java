package cn.lmjia.market.core.data_table;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @author CJ
 */
public class DataPageRequest extends PageRequest implements DataPageable {

    private final int draw;

    public DataPageRequest(int start, int length, int draw) {
        super(start / length, length);
        this.draw = draw;
    }

    public DataPageRequest(int start, int length, int draw, Sort.Direction direction, String... properties) {
        super(start / length, length, direction, properties);
        this.draw = draw;
    }

    public DataPageRequest(int start, int length, int draw, Sort sort) {
        super(start / length, length, sort);
        this.draw = draw;
    }

    @Override
    public int getDraw() {
        return draw;
    }
}
