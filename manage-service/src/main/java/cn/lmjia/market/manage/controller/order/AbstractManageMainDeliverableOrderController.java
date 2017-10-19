package cn.lmjia.market.manage.controller.order;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.order.MainDeliverableOrder;
import cn.lmjia.market.core.service.MainDeliverableOrderService;
import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.entity.StockShiftUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
public abstract class AbstractManageMainDeliverableOrderController<T extends MainDeliverableOrder> {

    @Autowired
    private LogisticsService logisticsService;
    @Autowired
    private MainDeliverableOrderService<T> mainDeliverableOrderService;

    String orderManageIndex(Model model) {
        model.addAttribute("title", managePageTitle());
        model.addAttribute("dataUri", dataUri());
        model.addAttribute("orderDeliveryUri", orderDeliveryUri());
        model.addAttribute("orderDetailUri", orderDetailUri());
        return "_orderManage.html";
    }

    /**
     * @return 订单详情uri
     */
    protected abstract String orderDetailUri();

    /**
     * @return 发货uri
     */
    protected abstract String orderDeliveryUri();

    /**
     * @return 请求数据的uri
     */
    protected abstract String dataUri();

    /**
     * @return 管理页面的标题
     */
    protected abstract String managePageTitle();

    /**
     * @return 管理页面的uri
     */
    protected abstract String managePageUri();

    String orderDelivery(Model model, long id) {
        logisticsService.viewModelForDelivery(mainDeliverableOrderService.getOrder(id), model);
        model.addAttribute("parentPageUri", managePageUri());
        model.addAttribute("parentPageName", managePageTitle());
        return "_orderDelivery.html";
    }

    String orderDetail(Model model, long id) {
        MainDeliverableOrder order = mainDeliverableOrderService.getOrder(id);
        model.addAttribute("parentPageUri", managePageUri());
        model.addAttribute("parentPageName", managePageTitle());
        model.addAttribute("currentData", order);
        if (order instanceof MainOrder) {
            model.addAttribute("mainOrder", order);
        }
        model.addAttribute("shipList", order.getLogisticsSet()
                .stream()
                .sorted(Comparator.comparing(StockShiftUnit::getCreateTime))
                .collect(Collectors.toList()));
        return "_orderDetail.html";
    }

}
