package cn.lmjia.market.core.controller.advice;

import cn.lmjia.market.core.model.ApiResult;
import cn.lmjia.market.core.trj.InvalidAuthorisingException;
import cn.lmjia.market.core.trj.TRJEnhanceConfig;
import com.huotu.verification.FrequentlySendException;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.wx.web.WeixinWebSpringConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CJ
 */
@ControllerAdvice
public class ControllerSupport {

    private static final Log log = LogFactory.getLog(ControllerSupport.class);

    @ExceptionHandler(InvalidAuthorisingException.class)
    public String sawInvalidAuthorisingException() {
        return "redirect:" + TRJEnhanceConfig.TRJOrderURI + "?InvalidAuthorisingException";
    }

    @ExceptionHandler(SystemMaintainException.class)
    public String sawSystemMaintainException(HttpServletRequest request, SystemMaintainException ex) {
        log.debug("发现系统维护异常", ex);
        if (WeixinWebSpringConfig.isWeixinRequest(request))
            return "wechat@error/systemMaintain.html";
        return "error/systemMaintain.html";
    }

    @ExceptionHandler(FrequentlySendException.class)
    @ResponseBody
    public ApiResult sawFrequentlySendException(FrequentlySendException ex) {
        return ApiResult.withCodeAndMessage(400, ex.getMessage(), null);
    }

    @ExceptionHandler(Throwable.class)
    public void all(Throwable ex) throws Throwable {
        log.debug("", ex);
        throw ex;
    }

}
