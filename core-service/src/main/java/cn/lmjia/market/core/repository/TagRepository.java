package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by helloztt on 2017-09-16.
 */
public interface TagRepository extends JpaRepository<Tag, String>, JpaSpecificationExecutor<Tag> {
    List<Tag> findByDisabledFalse();
}
