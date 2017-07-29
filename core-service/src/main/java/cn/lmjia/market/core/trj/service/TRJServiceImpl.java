package cn.lmjia.market.core.trj.service;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.trj.AuthorisingInfo;
import cn.lmjia.market.core.entity.trj.TRJPayOrder;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.repository.trj.AuthorisingInfoRepository;
import cn.lmjia.market.core.trj.InvalidAuthorisingException;
import cn.lmjia.market.core.trj.TRJService;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author CJ
 */
@Service
public class TRJServiceImpl implements TRJService {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    @Autowired
    private AuthorisingInfoRepository authorisingInfoRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MainOrderRepository mainOrderRepository;

    @Override
    public void addAuthorisingInfo(String authorising, String idNumber) {
        AuthorisingInfo info = new AuthorisingInfo();
        info.setId(authorising);
        info.setIdNumber(idNumber);
        info.setCreatedTime(LocalDateTime.now());
        authorisingInfoRepository.save(info);
    }

    @Override
    public AuthorisingInfo checkAuthorising(String authorising, String idNumber) throws InvalidAuthorisingException {
        AuthorisingInfo info = authorisingInfoRepository.findOne(authorising);
        if (info == null)
            throw new InvalidAuthorisingException(authorising, idNumber);
        if (info.isUsed())
            throw new InvalidAuthorisingException(authorising, idNumber);
        return info;
    }

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order
            , Map<String, Object> additionalParameters) throws SystemMaintainException {
        // 立即完成支付，同时告诉系统这个订单不能被结算！
        AuthorisingInfo info = (AuthorisingInfo) additionalParameters.get("info");
        synchronized (("AuthorisingInfo-" + info.getId()).intern()) {
            info = authorisingInfoRepository.getOne(info.getId());
            if (info.isUsed())
                throw new SystemMaintainException(new InvalidAuthorisingException(info.getId(), info.getIdNumber()));
            info.setUsed(true);
            info.setUsedTime(LocalDateTime.now());
            info = authorisingInfoRepository.save(info);
            TRJPayOrder payOrder = new TRJPayOrder();
            payOrder.setPlatformId(info.getId());
            payOrder.setAuthorisingInfo(info);
            // 需要立即完成支付
            executorService.schedule(()
                            -> applicationContext.getBean(PaymentService.class).mockPay(order)
                    , 1, TimeUnit.SECONDS);
            if (order instanceof MainOrder) {
                MainOrder mainOrder = (MainOrder) order;
                mainOrder.setDisableSettlement(true);
                mainOrderRepository.save(mainOrder);
            }
            return payOrder;
        }
    }

    @Override
    public void orderMaintain() {

    }
}
