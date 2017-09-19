package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.Tag;
import org.springframework.transaction.annotation.Transactional;

/**
 * 标签服务
 *
 * @author CJ
 */
public interface TagService {

    @Transactional
    Tag save(Tag tag);

    @Transactional
    Tag save(String tagName);

    @Transactional
    void addTagToGood(MainGood good, String[] tags);
}
