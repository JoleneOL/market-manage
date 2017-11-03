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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    @GetMapping("/manageCommonProblem")
    public String index() {
        return "operation-management/_helpCenter.html";
    }

    @GetMapping("/manageCommonProblem/List")
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
                        , Fields.asBasic("enable")
                        , FieldBuilder.asName(CommonProblem.class, "enableLabel")
                                .addSelect(CommonProblemRoot -> CommonProblemRoot.get(CommonProblem_.enable))
                                .addFormat((data, type) -> {
                                    boolean enable = (boolean) data;
                                    return enable ? "启用" : "禁用";
                                }).build()
                        , FieldBuilder.asName(CommonProblem.class, "isWeightLabel")
                                .addSelect(CommonProblemRoot -> CommonProblemRoot.get(CommonProblem_.isWeight))
                                .addFormat((data, type) -> {
                                    boolean isWeight = (boolean) data;
                                    return isWeight ? "展示" : "隐藏";
                                }).build()
                        , Fields.asBasic("isWeight")
                );
            }

            @Override
            public Specification<CommonProblem> specification() {
                return null;
            }
        };
    }


    @GetMapping("/manageCommonProblemAdd")
    public String indexForCreate() {
        return "operation-management/_helpDetail.html";
    }

    @GetMapping("/manageCommonProblemEdit")
    public String indexForEdit(long id, Model model) {
        model.addAttribute("currentData", commonProblemService.getOne(id));
        return "operation-management/_helpDetail.html";
    }

    @PostMapping("/manageCommonProblemSubmit")
    public String add(Long id, String title, String content) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("");
        }
        commonProblemService.addCommonProblem(id, title, content);
        return "redirect:/manageCommonProblem";
    }


}
