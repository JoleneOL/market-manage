package cn.lmjia.market.core.trj.controller;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.QuickTradeService;
import cn.lmjia.market.core.trj.TRJService;
import me.jiangcai.lib.ee.ServletUtils;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CJ
 */
@Controller
public class TRJEventController {

    private static final Log log = LogFactory.getLog(TRJEventController.class);
    @Autowired
    private Environment environment;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private TRJService trjService;
    @Autowired
    private QuickTradeService quickTradeService;
    @Autowired
    private MainOrderService mainOrderService;

    @PostMapping("/orderData/quickDone/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_MANAGER + "')")
    public void quickDone(@PathVariable("id") long id, String deliverCompany, String deliverStore, int stockQuantity
            , @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate shipmentTime
            , @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deliverTime) {
        quickTradeService.makeDone(mainOrderService.getOrder(id));
        trjService.deliverUpdate(id, deliverCompany, deliverStore, stockQuantity, shipmentTime, deliverTime);
    }

    @PostMapping("/_tourongjia_event_")
    @ResponseBody
    public Map<String, Object> event(HttpServletRequest request, String event, String authorising, String idNumber) {
        Map<String, Object> result = new HashMap<>();
        result.put("boolen", true);
        result.put("message", "OK");
        result.put("data", null);
        // 安全检查
        securityCheck(ServletUtils.clientIpAddress(request));

        if (event.equalsIgnoreCase("code")) {
            addCode(authorising, idNumber);
            return result;
        }

        return null;
    }

    private void addCode(String authorising, String idNumber) {
        log.debug("add Authorising Code:" + authorising + ":" + idNumber);
        trjService.addAuthorisingInfo(authorising, idNumber);
    }

    private void securityCheck(String ip) {
        // 测试环境 跳过
        if (environment.acceptsProfiles(CoreConfig.ProfileUnitTest) || environment.acceptsProfiles("staging"))
            return;
        String ips = systemStringService.getCustomSystemString("trj.ips", "trj.ips.comment", true, String.class, null);
        if (StringUtils.isEmpty(ips))
            throw new IllegalAccessError("bad access from " + ip);
        if (!Arrays.asList(ips.split(",")).contains(ip))
            throw new IllegalAccessError("bad access from " + ip);
    }

}
