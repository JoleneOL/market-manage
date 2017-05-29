package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.QuickTradeService;
import cn.lmjia.market.core.service.ReadService;
import me.jiangcai.payment.event.OrderPaySuccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 临时的快速服务
 *
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_MANAGER + "')")
public class QuickServiceController {

    private static final Log log = LogFactory.getLog(QuickServiceController.class);

    @Autowired
    private QuickTradeService quickTradeService;
    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private Environment environment;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ReadService readService;

    @PutMapping("/orderData/quickDone/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quickDone(@PathVariable("id") long id) {
        quickTradeService.makeDone(mainOrderService.getOrder(id));
    }


    @PutMapping("/orderData/mockPay/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void mockPay(@AuthenticationPrincipal Login login, @PathVariable("id") long id) {
        if (!environment.acceptsProfiles("allowMockPay"))
            return;
        MainOrder order = mainOrderService.getOrder(id);
        log.info(readService.nameForPrincipal(login) + "尝试模拟支付订单" + order.getSerialId());
        applicationEventPublisher.publishEvent(new OrderPaySuccess(order, null));
    }


}
