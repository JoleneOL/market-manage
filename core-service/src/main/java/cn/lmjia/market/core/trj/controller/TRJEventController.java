package cn.lmjia.market.core.trj.controller;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.trj.AuthorisingInfo;
import cn.lmjia.market.core.entity.trj.AuthorisingStatus;
import cn.lmjia.market.core.entity.trj.TRJPayOrder;
import cn.lmjia.market.core.trj.TRJService;
import me.jiangcai.lib.ee.ServletUtils;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

    @PostMapping("/_tourongjia_event_")
    @ResponseBody
    @Transactional
    public Map<String, Object> event(HttpServletRequest request, String event, String authorising, String idNumber
            , String message, @RequestParam(required = false) Boolean result
            , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time) {
        Map<String, Object> apiResult = new HashMap<>();
        apiResult.put("success", true);
        apiResult.put("message", "OK");
        apiResult.put("data", null);
        // 安全检查
        securityCheck(ServletUtils.clientIpAddress(request));

        if (event.equalsIgnoreCase("code")) {
            addCode(authorising, idNumber);
            return apiResult;
        }

        if (event.equalsIgnoreCase("v1")) {
            v1Check(authorising, result, message);
            return apiResult;
        }

        if (event.equalsIgnoreCase("v4")) {
            settlement(authorising, time);
            return apiResult;
        }


        throw new IllegalArgumentException();
    }

    private void settlement(String authorising, LocalDateTime time) {
        log.debug("settlement Result:" + authorising + " time:" + time);
        MainOrder order = trjService.findOrder(authorising);
        TRJPayOrder payOrder = (TRJPayOrder) order.getPayOrder();
        final AuthorisingInfo authorisingInfo = payOrder.getAuthorisingInfo();

        if (authorisingInfo.getAuthorisingStatus() != AuthorisingStatus.forSettle)
            throw new IllegalStateException("并未处于等待审核的状态");
        authorisingInfo.setAuthorisingStatus(AuthorisingStatus.settle);
        authorisingInfo.setSettlementTime(time);
        order.setDisableSettlement(false);
    }

    private void v1Check(String authorising, boolean result, String message) {
        log.debug("v1Check Result:" + authorising + " result:" + result + " ,message:" + message);
        MainOrder order = trjService.findOrder(authorising);
        TRJPayOrder payOrder = (TRJPayOrder) order.getPayOrder();
        final AuthorisingInfo authorisingInfo = payOrder.getAuthorisingInfo();
        if (authorisingInfo.getAuthorisingStatus() == AuthorisingStatus.Unused
                || authorisingInfo.getAuthorisingStatus() == AuthorisingStatus.forOrderComplete
                || authorisingInfo.getAuthorisingStatus() == AuthorisingStatus.forSettle
                || authorisingInfo.getAuthorisingStatus() == AuthorisingStatus.settle
                )
            throw new IllegalStateException("并未处于等待审核的状态:" + authorisingInfo.getAuthorisingStatus());
        authorisingInfo.setMessage(message);

        if (result)
            authorisingInfo.setAuthorisingStatus(AuthorisingStatus.forSettle);
        else {
            authorisingInfo.setAuditingTime(LocalDateTime.now());
            authorisingInfo.setAuthorisingStatus(AuthorisingStatus.auditingRefuse);
        }

        if (authorisingInfo.getAuthorisingStatus() == AuthorisingStatus.auditingRefuse)
            trjService.sendCheckWarningToCS(order, "信审被拒:" + message);
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
