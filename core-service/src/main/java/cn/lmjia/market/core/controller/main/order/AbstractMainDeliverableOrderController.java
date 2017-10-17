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
     * @return 可访问列表的uri
     */
    protected abstract String listUri();

    /**
     * @return 「列表」的标题
     */
    protected abstract String listTitle();

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

        // 默认的
        // 页面标题
        model.addAttribute("title", "我的下单");
        // 列表 标题
        model.addAttribute("listTitle", listTitle());
        // 列表 URI
        model.addAttribute("listUri", listUri());
        // 下单 标题
        model.addAttribute("orderTitle", "下&nbsp;&nbsp;单");
        // 下单 URI
        model.addAttribute("orderUri", "/wechatOrder");
        // 支付 URI
        model.addAttribute("payUri", "/wechatOrderPay");
    }

    /**
     *
     * @param model 相关model
     * @return 下单成功页面的视图名
     */
    protected String successView(Model model) {
        // 列表 标题
        model.addAttribute("listTitle", listTitle());
        // 列表 URI
        model.addAttribute("listUri", listUri());
        return "wechat@orderSuccess.html";
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

    protected MainGoodsAndAmounts getMainGoodAndAmounts(String[] goods, String[] goodsArray) {
        MainGoodsAndAmounts amounts;
        if (goods != null) {
            amounts = MainGoodsAndAmounts.ofArray(goods);
        } else if (goodsArray != null) {
            amounts = MainGoodsAndAmounts.ofArray(goodsArray);
        } else throw new IllegalArgumentException("goods required");
        return amounts;
    }
}
