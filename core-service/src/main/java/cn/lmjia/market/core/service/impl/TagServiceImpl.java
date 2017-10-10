package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.core.entity.support.TagType;
import cn.lmjia.market.core.repository.TagRepository;
import cn.lmjia.market.core.service.MainGoodService;
import cn.lmjia.market.core.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by helloztt on 2017/9/19.
 */
@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private MainGoodService mainGoodService;

    @Override
    public Tag save(String tagName) {
        Tag tag = tagRepository.findOne(tagName);
        if (tag == null) {
            tag = new Tag();
            tag.setName(tagName);
            tag.setType(TagType.SEARCH);
            tagRepository.save(tag);
        }
        return tag;
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public void addTagToGood(MainGood good, String[] tags) {
        if (good == null) {
            return;
        }
        if (tags == null || tags.length > 0) {
            good.setTags(null);
        } else {
            Set<Tag> newTagList = new HashSet<>();
            for (String tagName : tags) {
                newTagList.add(save(tagName));
            }
            good.setTags(newTagList);
        }
    }

    @Override
    public void delete(String name) {
        //找到所有用到这个标签的商品，删除它
        List<MainGood> goodList = mainGoodService.forSale(null, null, null, name);
        if (!CollectionUtils.isEmpty(goodList)) {
            goodList.forEach(good ->
                    good.getTags().removeIf(tag
                            -> tag.getName().equals(name)));
        }
        tagRepository.delete(name);
    }
}
