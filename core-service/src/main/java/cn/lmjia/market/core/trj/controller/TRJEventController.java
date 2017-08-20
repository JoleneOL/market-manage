package cn.lmjia.market.core.trj.controller;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.trj.TRJService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.lib.ee.ServletUtils;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private Environment environment;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private TRJService trjService;

    @PostMapping("/_tourongjia_event_")
    public ResponseEntity<?> event(HttpServletRequest request, String event, String authorising, String idNumber
            , String message, @RequestParam(required = false) Boolean result
            , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time) throws JsonProcessingException {
        Map<String, Object> apiResult = new HashMap<>();
        apiResult.put("success", true);
        apiResult.put("message", "OK");
        apiResult.put("data", null);

        try {
// 安全检查
            securityCheck(ServletUtils.clientIpAddress(request));

            if (event.equalsIgnoreCase("code")) {
                addCode(authorising, idNumber);
                return result(apiResult);
            }

            if (event.equalsIgnoreCase("v1")) {
                v1Check(authorising, result, message);
                return result(apiResult);
            }

            if (event.equalsIgnoreCase("v4")) {
                settlement(authorising, time);
                return result(apiResult);
            }

            throw new IllegalArgumentException("unsupported for:" + event);
        } catch (Exception ex) {
            log.trace("[TRJ]", ex);
            apiResult.put("success", false);
            apiResult.put("message", ex.getLocalizedMessage());
            return badResult(apiResult);
        }

    }

    private ResponseEntity<?> result(Map<String, Object> data) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(objectMapper.writeValueAsString(data));
    }

    private ResponseEntity<?> badResult(Map<String, Object> data) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(objectMapper.writeValueAsString(data));
    }

    private void settlement(String authorising, LocalDateTime time) {
        log.debug("settlement Result:" + authorising + " time:" + time);
        trjService.settlementResult(authorising, time);
    }

    private void v1Check(String authorising, boolean result, String message) {
        log.debug("v1Check Result:" + authorising + " result:" + result + " ,message:" + message);
        trjService.auditingResult(authorising, result, message);
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
