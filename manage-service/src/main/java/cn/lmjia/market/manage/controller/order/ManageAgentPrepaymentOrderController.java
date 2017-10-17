package cn.lmjia.market.manage.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_LOGISTICS + "','" + Login.ROLE_SUPPLY_CHAIN + "')")
public class ManageAgentPrepaymentOrderController extends AbstractManageMainDeliverableOrderController<AgentPrepaymentOrder> {

    @GetMapping("/agentPrepaymentOrderDelivery")
    @Transactional(readOnly = true)
    public String mainOrderDelivery(long id, Model model) {
        // 物流模块提供数据，视图层依然由客户端项目，期待将来更高级视图解决方案的设计
        return orderDelivery(model, id);
    }

    @GetMapping("/agentPrepaymentOrderDetail")
    @Transactional(readOnly = true)
    public String mainOrderDetail(long id, Model model) {
        return orderDetail(model, id);
    }

    @Override
    protected String orderDetailUri() {
        return "/agentPrepaymentOrderDetail";
    }

    @Override
    protected String orderDeliveryUri() {
        return "/agentPrepaymentOrderDelivery";
    }

    @Override
    protected String dataUri() {
        return "/orderData2/manageableList";
    }

    @Override
    protected String managePageTitle() {
        return "批货订单管理";
    }

    @Override
    protected String managePageUri() {
        return "/agentPrepaymentOrderManage";
    }


    @GetMapping("/agentPrepaymentOrderManage")
    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_LOGISTICS + "','" + Login.ROLE_SUPPLY_CHAIN + "','" + Login.ROLE_LOOK + "')")
    public String orderManage(Model model) {
        return orderManageIndex(model);
    }

}
