package cn.lmjia.market.manage.controller.help;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.help.CommonProblem;
import cn.lmjia.market.core.entity.help.CommonProblem_;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.service.help.CommonProblemService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 常见问题管理
 *
 * @author lxf
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_PRODUCT_CENTER + "')")
public class ManageCommonProblemController {

    @Autowired
    private CommonProblemService commonProblemService;

    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_PRODUCT_CENTER + "','" + Login.ROLE_LOOK + "')")
    @GetMapping("/manage/commonProblem")
    public String index() {
        return "operation-management/_helpCenter.html";
    }

    @GetMapping("/manage/commonProblemList")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition<CommonProblem> data() {
        return new RowDefinition<CommonProblem>() {
            @Override
            public Class<CommonProblem> entityClass() {
                return CommonProblem.class;
            }

            @Override
            public List<FieldDefinition<CommonProblem>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(CommonProblem.class, "title")
                                .build()
                        , FieldBuilder.asName(CommonProblem.class, "weight")
                                .build()
                        , Fields.asBasic("enable")
                        , FieldBuilder.asName(CommonProblem.class, "enableLabel")
                                .addSelect(CommonProblemRoot -> CommonProblemRoot.get(CommonProblem_.enable))
                                .addFormat((data, type) -> {
                                    boolean enable = (boolean) data;
                                    return enable ? "启用" : "禁用";
                                }).build()
                        , FieldBuilder.asName(CommonProblem.class, "isHotLabel")
                                .addSelect(CommonProblemRoot -> CommonProblemRoot.get(CommonProblem_.hot))
                                .addFormat((data, type) -> {
                                    boolean hot = (boolean) data;
                                    return hot ? "展示" : "隐藏";
                                }).build()
                        , Fields.asBasic("hot")
                );
            }

            @Override
            public Specification<CommonProblem> specification() {
                return null;
            }
        };
    }


    @GetMapping("/manage/commonProblemAdd")
    public String indexForCreate() {
        return "operation-management/_helpDetail.html";
    }

    @GetMapping("/manage/commonProblemEdit")
    public String indexForEdit(long id, Model model) {
        model.addAttribute("currentData", commonProblemService.getOne(id));
        return "operation-management/_helpDetail.html";
    }

    @PostMapping("/manage/commonProblemSubmit")
    public String add(Long id, String title, Integer weight,String content) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("");
        }
        weight = weight == null ? 50 : weight;
        commonProblemService.addAndEditCommonProblem(id, title, weight,content);
        return "redirect:/manage/commonProblem";
    }


    @PutMapping("/help/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void enable(@PathVariable("id") long id) {
        CommonProblem commonProblem = commonProblemService.getOne(id);
        commonProblem.setEnable(true);
    }

    @PutMapping("/help/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void disable(@PathVariable("id") long id) {
        CommonProblem commonProblem = commonProblemService.getOne(id);
        commonProblem.setEnable(false);
    }

    @PutMapping("/help/{id}/isHotLabel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void isHotLabel(@PathVariable("id") long id) {
        CommonProblem commonProblem = commonProblemService.getOne(id);
        commonProblem.setHot(true);
    }

    @PutMapping("/help/{id}/notHotLabel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void notHotLabel(@PathVariable("id") long id) {
        CommonProblem commonProblem = commonProblemService.getOne(id);
        commonProblem.setHot(false);
    }
}
