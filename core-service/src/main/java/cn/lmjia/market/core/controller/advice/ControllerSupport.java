package cn.lmjia.market.core.controller.advice;

import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.wx.web.WeixinWebSpringConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CJ
 */
@ControllerAdvice
public class ControllerSupport {

    private static final Log log = LogFactory.getLog(ControllerSupport.class);

    @ExceptionHandler(SystemMaintainException.class)
    public String sawSystemMaintainException(HttpServletRequest request, SystemMaintainException ex) {
        log.debug("发现系统维护异常", ex);
        if (WeixinWebSpringConfig.isWeixinRequest(request))
            return "wechat@error/systemMaintain.html";
        return "error/systemMaintain.html";
    }

}
