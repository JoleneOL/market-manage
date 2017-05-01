package cn.lmjia.market.core.data_table;

import cn.lmjia.market.core.selection.PageAndSelection;
import cn.lmjia.market.core.selection.Selection;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author CJ
 */
public class DrawablePageAndSelection<T> extends PageAndSelection<T> {

    @Getter
    private final DataPageable dataPageable;

    public DrawablePageAndSelection(DataPageable dataPageable, Page<T> page, List<Selection<T>> selections) {
        super(page, selections);
        this.dataPageable = dataPageable;
    }
}
