package cn.lmjia.market.core.controller.advice;

import cn.lmjia.market.core.exception.MainGoodLimitStockException;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.model.ApiResult;
import cn.lmjia.market.core.trj.InvalidAuthorisingException;
import cn.lmjia.market.core.trj.TRJEnhanceConfig;
import com.huotu.verification.FrequentlySendException;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.wx.web.WeixinWebSpringConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author CJ
 */
@ControllerAdvice
public class ControllerSupport {

    private static final Log log = LogFactory.getLog(ControllerSupport.class);

    @ExceptionHandler(InvalidAuthorisingException.class)
    public String sawInvalidAuthorisingException(HttpServletRequest request, HttpServletResponse response) {
        boolean isAjax = isAjaxRequestOrBackJson(request);
        if(isAjax){
            ApiResult result = ApiResult.withCodeAndMessage(402,null,null);
            try {
                response.getWriter().write(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
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

    @ExceptionHandler(MainGoodLowStockException.class)
    @ResponseBody
    public ApiResult sawMainGoodLimitStockException(MainGoodLowStockException ex){
        String message = "存在商品库存不足";
        if(ex instanceof MainGoodLimitStockException){
            message = "存在商品下单数量超过限购数量";
        }
        return ApiResult.withCodeAndMessage(401,message,ex.toData());
    }

    @ExceptionHandler(Throwable.class)
    public void all(Throwable ex) throws Throwable {
        log.debug("", ex);
        throw ex;
    }

    /***
     * 判断当前请求是http请求还是ajax请求
     * @param request
     * @return
     */
    private boolean isAjaxRequestOrBackJson(HttpServletRequest request) {
        String x_request_with = request.getHeader("X-Requested-With");
        if (!StringUtils.isEmpty(x_request_with) && x_request_with.toLowerCase().contains("xmlhttprequest"))
            return true;
        return false;
    }

}
