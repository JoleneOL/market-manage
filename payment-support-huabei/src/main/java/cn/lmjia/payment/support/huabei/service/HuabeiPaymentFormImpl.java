package cn.lmjia.payment.support.huabei.service;

import cn.lmjia.payment.support.huabei.HuabeiPaymentForm;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author CJ
 */
@Service
public class HuabeiPaymentFormImpl implements HuabeiPaymentForm {

    /**
     * 通讯URL
     */
    private final String rootUrl;
    /**
     * 商户编码
     */
    private final String businessID;
    /**
     * 收款门店编码
     */
    private final String shopID;
    /**
     * 收款支付宝PID
     */
    private final String aliPid;

    @Autowired
    public HuabeiPaymentFormImpl(Environment environment) {
        rootUrl = environment.getProperty("huabei.url", "http://localhost");
        businessID = environment.getProperty("huabei.businessID", "HBCD0001");
        shopID = environment.getProperty("huabei.shopID", "");
        aliPid = environment.getProperty("huabei.aliPid", "");
    }

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order
            , Map<String, Object> additionalParameters) throws SystemMaintainException {
        // 商户预创建订单时候的订单号，将作为本订单的唯一标识格式为:yyMMddHHmmssSSS+5位随机数


        return null;
    }

    @Override
    public void orderMaintain() {

    }
}
