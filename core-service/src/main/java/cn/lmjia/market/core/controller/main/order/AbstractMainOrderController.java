package cn.lmjia.market.core.controller.main.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.channel.InstallmentChannel;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.model.MainGoodsAndAmounts;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.MainGoodService;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.SalesmanService;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.wx.model.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author CJ
 */
public abstract class AbstractMainOrderController {
    @Autowired
    private MainGoodRepository mainGoodRepository;
    @Autowired
    protected MainOrderService mainOrderService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private SalesmanService salesmanService;

    protected MainOrder from(String orderId, Long id) {
        if (id != null)
            return mainOrderService.getOrder(id);
        if (!StringUtils.isEmpty(orderId))
            return mainOrderService.getOrder(orderId);
        return null;
    }

    /**
     * 展示下单页面
     *
     * @param login   当前身份
     * @param model   model
     * @param channel 特定渠道
     */
    protected void orderIndex(Login login, Model model, Channel channel) {
        model.addAttribute("goodList", mainGoodService.forSale(channel));
        model.addAttribute("channel", channel);
        // 该量表示 渠道已经支持分期；如果这样的话，支付时不应该提供分期选项
        model.addAttribute("installmentSupported", (channel != null) && channel instanceof InstallmentChannel);
        model.addAttribute("salesAchievement", salesmanService.pick(login));
    }

    protected MainOrder newOrder(Login login, Model model, long recommendId, String name, int age, Gender gender
            , Address address, String mobile, String mortgageIdentifier, Long channelId
            , MainGoodsAndAmounts amounts) throws MainGoodLowStockException {
        Map<MainGood, Integer> realAmounts = amounts.toReal(mainGoodRepository);
        realAmounts.keySet().forEach(good -> {
            if ((channelId != null) && (good.getChannel() == null || !good.getChannel().getId().equals(channelId))) {
                throw new IllegalArgumentException("特定的频道只能购买特定的商品");
            }
        });

        return mainOrderService.newOrder(login, loginService.get(recommendId), name, mobile, age
                , gender, address
                , realAmounts, mortgageIdentifier);
    }
}
