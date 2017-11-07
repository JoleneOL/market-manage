package cn.lmjia.market.core.repository.help;

import cn.lmjia.market.core.entity.help.CommonProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author lxf
 */
public interface CommonProblemRepository extends JpaRepository<CommonProblem,Long>,JpaSpecificationExecutor<CommonProblem>,PagingAndSortingRepository<CommonProblem,Long> {
}
