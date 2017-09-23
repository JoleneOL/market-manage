package cn.lmjia.market.wechat.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;

/**
 * 微信端业绩相关的控制器
 *
 * @author CJ
 */
@Controller
public class WechatSalesAchievementController {

    private static final Log log = LogFactory.getLog(WechatSalesAchievementController.class);

    @GetMapping("/api/salesList")
    @ResponseBody
    public Object list(@RequestParam(required = false) LocalDate date, Boolean remark, Boolean deal) {
        if (log.isTraceEnabled()) {
            log.trace("date:" + date + ", remark:" + remark + ", deal:" + deal);
        }
        return null;
    }

}
