package cn.lmjia.market.core.selection;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author CJ
 */
public class PageAndSelection<T> {
    @Getter
    private final Page<T> page;
    @Getter
    private final List<Selection<T>> selectionList;

    public PageAndSelection(Page<T> page, List<Selection<T>> selectionList) {
        this.page = page;
        this.selectionList = selectionList;
    }
}
