package me.jiangcai.logistics.repository_util;

import me.jiangcai.logistics.entity.Depot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface AbstractDepotRepository<T extends Depot> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    List<T> findByEnableTrue();
}
