package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 标签管理
 * 标签只能增加和删除，无法修改
 * Created by helloztt on 2017-09-16.
 */
@Controller
@PreAuthorize("hasRole('ROOT')")
public class ManageTagController {
    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/manageTag")
    public String index(){
        return "_tagManage.html";
    }

}
