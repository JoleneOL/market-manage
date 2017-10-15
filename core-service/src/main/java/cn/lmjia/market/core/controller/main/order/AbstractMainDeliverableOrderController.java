package cn.lmjia.market.core.controller.main.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.channel.InstallmentChannel;
import cn.lmjia.market.core.entity.order.MainDeliverableOrder;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.model.MainGoodsAndAmounts;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.MainDeliverableOrderService;
import cn.lmjia.market.core.service.MainGoodService;
import cn.lmjia.market.core.service.SalesmanService;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.wx.model.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.Map;

/**
 * @author CJ
 */
public abstract class AbstractMainDeliverableOrderController<T extends MainDeliverableOrder> {
    @Autowired
    protected MainDeliverableOrderService<T> mainDeliverableOrderService;
    @Autowired
    private MainGoodRepository mainGoodRepository;
    @Autowired
    private LoginService loginService;
    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private SalesmanService salesmanService;

    /**
     * @param orderId 字符串id
     * @param id      直接id
     * @return 获取具体订单
     */
    protected abstract T from(String orderId, Long id);

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

    protected T newOrder(Login login, Model model, long recommendId, String name, int age, Gender gender
            , Address address, String mobile, String mortgageIdentifier, Long channelId
            , MainGoodsAndAmounts amounts) throws MainGoodLowStockException {
        Map<MainGood, Integer> realAmounts = amounts.toReal(mainGoodRepository);
        realAmounts.keySet().forEach(good -> {
            if ((channelId != null) && (good.getChannel() == null || !good.getChannel().getId().equals(channelId))) {
                throw new IllegalArgumentException("特定的频道只能购买特定的商品");
            }
        });

        return mainDeliverableOrderService.newOrder(login, loginService.get(recommendId), name, mobile, age
                , gender, address
                , realAmounts, mortgageIdentifier);
    }
}
