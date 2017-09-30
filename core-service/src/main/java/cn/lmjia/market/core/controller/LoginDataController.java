package cn.lmjia.market.core.controller;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Login_;
import cn.lmjia.market.core.entity.settlement.LoginCommissionJournal;
import cn.lmjia.market.core.repository.settlement.LoginCommissionJournalRepository;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.Select2Dramatizer;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.ReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 身份数据相关控制器
 *
 * @author CJ
 */
@Controller
public class LoginDataController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private LoginCommissionJournalRepository loginCommissionJournalRepository;

    /**
     * 公开可用的手机号码可用性校验
     *
     * @param mobile 确认的手机号码
     * @return 可用性
     */
    @GetMapping("/loginData/mobileValidation")
    @ResponseBody
    public boolean mobileValidation(@RequestParam String mobile) {
        return loginService.mobileValidation(mobile);
    }

    @PreAuthorize("!isAnonymous()")
    @GetMapping("/loginData/select2")
    @RowCustom(dramatizer = Select2Dramatizer.class, distinct = true)
    public RowDefinition<Login> searchLoginSelect2(String search) {
        return searchLogin(search);
    }

    @GetMapping(value = "/loginCommissionJournal", produces = "text/html")
    @Transactional(readOnly = true)
    public String journal(long id, @AuthenticationPrincipal Login login, Model model) {
        // 自己只可以查自己的
        if (!login.isManageable() && login.getId() != id)
            throw new AccessDeniedException("不可以查看别人的流水");
        final List<LoginCommissionJournal> list = loginCommissionJournalRepository.findByLoginOrderByHappenTimeAsc(loginService.get(id));
        model.addAttribute("list", list);

        BigDecimal current = BigDecimal.ZERO;
        // 用于保存当时的数据
        Map<String, Money> currentData = new HashMap<>();
        for (LoginCommissionJournal journal : list) {
            current = current.add(journal.getChanged());
            currentData.put(journal.getId(), new Money(current));
        }
        model.addAttribute("currentData", currentData);

        return "mock/journal.html";
    }

    /**
     * 查询所有用户
     *
     * @param search 可能是名字或者电话号码
     * @return 字段定义
     */
    private RowDefinition<Login> searchLogin(String search) {
        return new RowDefinition<Login>() {
            @Override
            public Class<Login> entityClass() {
                return Login.class;
            }

            @Override
            public List<FieldDefinition<Login>> fields() {
                return Arrays.asList(Fields.asBasic("id")
                        , FieldBuilder.asName(Login.class, "name")
                                .addBiSelect(ReadService::nameForLogin)
                                .build()
                        , FieldBuilder.asName(Login.class, "mobile")
                                .addBiSelect(ReadService::mobileForLogin)
                                .build()
                );
            }

            @Override
            public Specification<Login> specification() {
                if (StringUtils.isEmpty(search))
                    return null;
//                    return (root, query, cb) -> {
//                        // 必须得有 所以right
//                        Join<Login, ContactWay> contactWayJoin = root.join("contactWay", JoinType.INNER);
//                        return cb.isNotNull(contactWayJoin);
//                    };
                String jpaSearch = "%" + search + "%";
                return (root, query, cb) -> {
                    // 必须得有 所以right
                    Join<Login, ContactWay> contactWayJoin = root.join("contactWay", JoinType.LEFT);
                    return cb.or(
                            cb.like(contactWayJoin.get("mobile"), jpaSearch)
                            , cb.like(contactWayJoin.get("name"), jpaSearch)
                            , cb.like(root.get(Login_.loginName), jpaSearch)
                    );
                };
            }
        };
    }


}
