package me.jiangcai.logistics.repository_util;

import me.jiangcai.logistics.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface AbstractProductRepository<T extends Product> extends JpaRepository<T, String>
        , JpaSpecificationExecutor<T> {
    List<T> findByEnableTrue();

    List<T> findByProductTypeNull();
}
