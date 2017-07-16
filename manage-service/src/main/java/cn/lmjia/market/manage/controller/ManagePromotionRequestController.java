package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.dealer.entity.PromotionRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 代理商管理员可进行
 *
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_AllAgent + "','" + Login.ROLE_PROMOTION + "')")
public class ManagePromotionRequestController {

    @GetMapping("/managePromotionRequest")
    public String index() {
        return "_agentUpdate.html";
    }

    /**
     * @param applicationDate quarter,month,all(一年)
     * @return
     */
    @GetMapping("/manage/promotionRequests")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition data(String applicationDate, String mobile) {
        return PromotionRequest.Rows(applicationDate, mobile);
    }


}
