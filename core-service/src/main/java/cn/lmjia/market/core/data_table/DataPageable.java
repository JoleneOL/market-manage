package cn.lmjia.market.core.data_table;

import org.springframework.data.domain.Pageable;

/**
 * 参考<a href="https://datatables.net/examples/data_sources/server_side.html">jQuery DataTables技术</a>
 * 特指一种可描述的分页
 *
 * @author CJ
 */
public interface DataPageable extends Pageable {
    int getDraw();
}
