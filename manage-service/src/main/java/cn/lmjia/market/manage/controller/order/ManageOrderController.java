package cn.lmjia.market.manage.controller.order;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.service.MainOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasRole('ROOT')")
public class ManageOrderController {

    @Autowired
    private MainOrderService mainOrderService;

    @GetMapping("/orderData/logistics/{orderId}")
    @ResponseBody
    @Transactional(readOnly = true)
    public Object preLogistics(@PathVariable("orderId") long orderId) {
        Map<String, Object> data = new HashMap<>();
        MainOrder order = mainOrderService.getOrder(orderId);
        data.put("depots", mainOrderService.depotsForOrder(orderId).stream()
                .filter(stockInfo -> stockInfo.getAmount() >= order.getAmount())
                // 库存多的优先
                .sorted((o1, o2) -> o2.getAmount() - o1.getAmount())
                .map(info -> {
                    Map<String, Object> x = new HashMap<>();
                    x.put("id", info.getDepot().getId());
                    x.put("name", info.getDepot().getName());
                    x.put("quantity", info.getAmount());
                    x.put("distance", -1);
                    return x;
                })
                .collect(Collectors.toSet())
        );
        return data;
    }

    @PutMapping("/orderData/logistics/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeLogistics(@PathVariable("orderId") long orderId, @RequestBody String depotId) {
        mainOrderService.makeLogistics(orderId, NumberUtils.parseNumber(depotId, Long.class));
    }

}
