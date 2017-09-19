package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.core.entity.support.TagType;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.repository.TagRepository;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.rows.TagRows;
import cn.lmjia.market.core.service.MainGoodService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理
 * 标签只能增加和删除，无法修改
 * Created by helloztt on 2017-09-16.
 */
@Controller
@PreAuthorize("hasRole('ROOT')")
public class ManageTagController {
    private static final Log log = LogFactory.getLog(ManageTagController.class);
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private MainGoodRepository mainGoodRepository;

    @GetMapping("/manageTag")
    public String index() {
        return "_tagManage.html";
    }

    @GetMapping("/manageTagAdd")
    public String toAdd() {
        return "_tagAdd.html";
    }

    @GetMapping("/manage/tagList")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition data() {
        return new TagRows(time -> conversionService.convert(time, String.class)) {
            @Override
            public Specification<Tag> specification() {
                return null;
            }
        };
    }

    @PostMapping("/manage/tagList")
    public String add(@RequestParam String name, @RequestParam Integer type
            , @RequestParam(required = false, defaultValue = "0") Integer weight) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setType(TagType.values()[type]);
        tag.setWeight(weight);
        tagRepository.save(tag);
        return "redirect:/manageTag";
    }


    @PutMapping("/manage/tagList/{name}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @Transactional
    // TODO: 2017/9/17 这里有个疑问等待解决
    public void delete(@PathVariable("name") String name) {
        //找到所有用到这个标签的商品，删除它
        List<MainGood> goodList = mainGoodService.forSearch(name);
        if (!CollectionUtils.isEmpty(goodList)) {
            goodList.forEach(good -> good.getTags().remove(name));
//            mainGoodRepository.save(goodList);
        }
        tagRepository.delete(name);
    }

    @PutMapping("/manage/tagList/{name}/check")
    @ResponseBody
    public String checkName(@PathVariable("name") String name) {
        return (StringUtils.isEmpty(name) || tagRepository.findOne(name) != null) ? "false" : "true";
    }

    @PutMapping("/manage/tagList/{name}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void disable(@PathVariable("name") String name) {
        tagRepository.getOne(name).setDisabled(true);
    }

    @PutMapping("/manage/tagList/{name}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void enable(@PathVariable("name") String name) {
        //删除所有用到这个标签的商品的标签
        tagRepository.getOne(name).setDisabled(false);
    }

}
