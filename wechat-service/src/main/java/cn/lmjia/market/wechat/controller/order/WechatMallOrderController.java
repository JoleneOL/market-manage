package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.service.MainGoodService;
import cn.lmjia.market.core.service.SystemService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by helloztt on 2017/9/28.
 */
@Controller
public class WechatMallOrderController {
    @Autowired
    private MainGoodService mainGoodService;

    /**
     * @return 展示下单页面
     */
    @PostMapping(SystemService.mallOrderURi)
    public String index(@AuthenticationPrincipal Login login, @RequestParam String order, Model model) {
        //只有存在用户名才能下单
        if (StringUtils.isEmpty(login.getUsername())) {
            return "redirect:/wechatRegister";
        }
        Map<MainGood, Long> cartGoodsMap = new HashMap<>();
        JSONObject object = JSONObject.parseObject(order);
        object.keySet().forEach(key -> {
            MainGood mainGood = mainGoodService.findOne(Long.valueOf(key));
            if (mainGood != null) {
                cartGoodsMap.put(mainGood, object.getLong(key));
            }
        });
        model.addAttribute("goodsMap", cartGoodsMap);
        double totalPrice = cartGoodsMap.keySet().stream()
                .mapToDouble(goods -> goods.getTotalPrice().multiply(new BigDecimal(cartGoodsMap.get(goods))).doubleValue())
                .sum();
        model.addAttribute("totalPrice", totalPrice);
        return "wechat@mall/orderPlace.html";
    }
}
