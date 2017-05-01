package cn.lmjia.market.core.selection;

import lombok.Data;

import java.util.List;

/**
 * @author CJ
 */
@Data
public class Selections<T> {
    private List<Selection<T>> list;

}
