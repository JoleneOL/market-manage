package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by helloztt on 2017-09-16.
 */
public interface TagRepository extends JpaRepository<Tag, String>, JpaSpecificationExecutor<Tag> {
}
