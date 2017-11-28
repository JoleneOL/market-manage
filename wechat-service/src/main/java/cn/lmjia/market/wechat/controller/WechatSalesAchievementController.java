package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.SalesAchievement;
import cn.lmjia.market.core.service.SalesmanService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.util.ApiDramatizer;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

/**
 * 微信端业绩相关的控制器
 *
 * @author CJ
 */
@Controller
public class WechatSalesAchievementController {

    private static final Log log = LogFactory.getLog(WechatSalesAchievementController.class);

    @Autowired
    private SalesmanService salesmanService;

    @GetMapping("/api/salesList")
    @RowCustom(dramatizer = ApiDramatizer.class, distinct = true)
    public RowDefinition<SalesAchievement> list(@AuthenticationPrincipal Login login
            , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Boolean remark, Boolean deal) {
        if (log.isTraceEnabled()) {
            log.trace("date:" + date + ", remark:" + remark + ", deal:" + deal);
        }
        return salesmanService.data(login, date, remark, deal);
    }

    @PutMapping("/salesAchievement/{id}/remark")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRemark(@AuthenticationPrincipal Login login, @RequestBody String text, @PathVariable("id") long id) {
        SalesAchievement salesAchievement = salesmanService.getAchievement(id);
        if (salesAchievement.getWhose().getLogin().equals(login)) {
            salesAchievement.setRemark(text);
        }
    }

    @GetMapping(SystemService.wechatSales)
    public String index() {
        return "wechat@salesAchievement.html";
    }

}
