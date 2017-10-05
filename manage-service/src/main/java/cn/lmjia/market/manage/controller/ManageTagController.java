package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.core.entity.support.TagType;
import cn.lmjia.market.core.repository.TagRepository;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.rows.TagRows;
import cn.lmjia.market.core.service.MainGoodService;
import cn.lmjia.market.core.service.TagService;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.seext.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

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
    private TagService tagService;
    @Autowired
    private ResourceService resourceService;

    @GetMapping("/manageTag")
    public String index() {
        return "_tagManage.html";
    }

    @GetMapping("/manageTagAdd")
    public String toAdd() {
        return "_tagOperator.html";
    }

    @GetMapping("/manage/tagDetail")
    public String detail(@RequestParam String name, Model model) {
        model.addAttribute("currentData", tagRepository.findOne(name));
        return "_tagDetail.html";
    }

    @GetMapping("/manage/tagEdit")
    public String edit(@RequestParam String name, Model model) {
        model.addAttribute("currentData", tagRepository.findOne(name));
        return "_tagOperator.html";
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
    @Transactional
    public String add(@RequestParam String name, Integer type
            , @RequestParam(required = false, defaultValue = "0") Integer weight, String icon) throws IOException {

        Tag tag = tagRepository.findOne(name);
        if (tag == null) {
            tag = new Tag();
            tag.setName(name);
            tag.setType(TagType.values()[type]);
        }
        tag.setWeight(weight);
        tag = tagRepository.saveAndFlush(tag);

        //转存资源
        if (!StringUtils.isEmpty(icon) && !icon.equalsIgnoreCase(tag.getIcon())) {
            String tagImgResource = "tag/" + tag.getName() + "." + FileUtils.fileExtensionName(icon);
            resourceService.moveResource(tagImgResource, icon);
            tag.setIcon(tagImgResource);
        }
        return "redirect:/manageTag";
    }

    @PostMapping("/manage/addTag")
    @ResponseBody
    public String add(String name) {
        if ("false".equals(checkName(name))) {
            return "false";
        }
        tagService.save(name);
        return "true";
    }


    @DeleteMapping("/manage/tagList")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam String name) {
        try {
            tagService.delete(name);
        } catch (Throwable ignored) {
            log.info("删除时错误", ignored);
            // 为什么选择忽略它，因为它并非核心业务；基于什么关联的 可以通过DBA直接设置级联操作
        }

    }

    @GetMapping("/manage/tagList/check")
    @ResponseBody
    public String checkName(@RequestParam String name) {
        return (StringUtils.isEmpty(name) || tagRepository.findOne(name) != null) ? "false" : "true";
    }

    @PutMapping("/manage/tagList/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void disable(@RequestParam String name) {
        tagRepository.getOne(name).setDisabled(true);
    }

    @PutMapping("/manage/tagList/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void enable(@RequestParam String name) {
        //删除所有用到这个标签的商品的标签
        tagRepository.getOne(name).setDisabled(false);
    }

}
