package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.ScriptTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface ScriptTaskRepository extends JpaRepository<ScriptTask, Long>, JpaSpecificationExecutor<ScriptTask> {

    long countByName(String name);
}
