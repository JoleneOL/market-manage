package cn.lmjia.market.core.service;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 支付助手服务
 *
 * @author CJ
 */
public interface PayAssistanceService {

    /**
     * 发起支付
     *
     * @param openId  openId
     * @param request mvc请求
     * @param order   需支付订单
     * @return 视图
     * @throws SystemMaintainException
     */
    ModelAndView payOrder(String openId, HttpServletRequest request, PayableOrder order) throws SystemMaintainException;
}
