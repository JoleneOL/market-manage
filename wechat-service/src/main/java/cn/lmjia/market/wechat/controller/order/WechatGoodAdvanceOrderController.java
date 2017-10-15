package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.controller.main.order.AbstractMainDeliverableOrderController;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.exception.MainGoodLowStockException;
import cn.lmjia.market.core.model.ApiResult;
import cn.lmjia.market.core.model.MainGoodsAndAmounts;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.rows.AgentPrepaymentOrderRows;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.trj.InvalidAuthorisingException;
import cn.lmjia.market.core.util.ApiDramatizer;
import com.alibaba.fastjson.JSONObject;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.lib.spring.data.AndSpecification;
import me.jiangcai.wx.model.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author CJ
 */
@Controller
public class WechatGoodAdvanceOrderController extends AbstractMainDeliverableOrderController<AgentPrepaymentOrder> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.CHINA);

    @Autowired
    private ConversionService conversionService;

    /**
     * @return 展示下单页面
     */
    @GetMapping("/wechatAgentPrepaymentOrder")
    public String index(@AuthenticationPrincipal Login login, Model model) {
        model.addAttribute("trj", false);
        model.addAttribute("huabeiEnable", false);
        orderIndex(login, model, null);
        return "wechat@orderPlace.html";
    }

    /**
     * 微信下单
     *
     * @param name              客户姓名
     * @param gender            客户性别
     * @param address           地址
     * @param mobile            手机号
     * @param activityCode      可选的按揭识别码
     * @param channelId         渠道
     * @param authorising       按揭码
     * @param idNumber          身份证
     * @param installmentHuabai 是否使用花呗支付
     * @param goods             下单商品
     * @return 创建订单结果
     * @throws MainGoodLowStockException   库存不足异常
     * @throws InvalidAuthorisingException 按揭码校验异常
     */
    @PostMapping("/wechatAgentPrepaymentOrder")
    @ResponseBody
    @Transactional
    public ApiResult newOrder(String name, Gender gender
            , Address address, String mobile, String activityCode, @AuthenticationPrincipal Login login, Model model
            , @RequestParam(required = false) Long channelId
            , String authorising, String idNumber, boolean installmentHuabai
            , String[] goods, @RequestParam(name = "goods[]", required = false) String[] goodsArray)
            throws MainGoodLowStockException, InvalidAuthorisingException {
        int age = 20;
        MainGoodsAndAmounts amounts = getMainGoodAndAmounts(goods, goodsArray);
        AgentPrepaymentOrder order = newOrder(login, model, login.getId(), name, age, gender, address, mobile,
                activityCode, channelId, amounts);
        JSONObject result = new JSONObject();
        result.put("id", order.getId());
        if (channelId != null) {
            //校验按揭码
//            payAssistanceService.checkAuthorising(authorising, idNumber);
            result.put("channelId", channelId);
            result.put("idNumber", idNumber);
            result.put("authorising", authorising);
        }
        result.put("installmentHuabai", installmentHuabai);
        return ApiResult.withCode(200, result);
    }

    @Override
    protected void orderIndex(Login login, Model model, Channel channel) {
        super.orderIndex(login, model, channel);
        // 页面标题
        model.addAttribute("title", "批货下单");
        // 列表 标题
        model.addAttribute("listTitle", "批货订单");
        // 列表 URI
        model.addAttribute("listUri", SystemService.goodAdvanceOrderList);
        // 下单 标题
        model.addAttribute("orderTitle", "批&nbsp;&nbsp;货");
        // 下单 URI
        model.addAttribute("orderUri", "/wechatAgentPrepaymentOrder");
    }

    /**
     * @return 仅仅显示我的订单
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/agentPrepaymentOrderList")
    @RowCustom(distinct = true, dramatizer = ApiDramatizer.class)
    public RowDefinition myOrder(@AuthenticationPrincipal Login login, String search, OrderStatus status) {
        return new AgentPrepaymentOrderRows(t -> t.format(formatter)) {
            @Override
            public Specification<AgentPrepaymentOrder> specification() {
                return new AndSpecification<>(
                        mainDeliverableOrderService.search(search, status)
                        , (root, query, cb) -> cb.equal(root.get("orderBy"), login)
                );
            }
        };
    }

    @GetMapping(SystemService.goodAdvanceOrderList)
    public String list() {
        return "wechat@goodAdvanceOrderList.html";
    }

    @GetMapping("/wechatAgentPrepaymentOrderDetail")
    public String detail(String orderId, Model model) {
        model.addAttribute("order", from(orderId, null));
        return "wechat@orderDetail.html";
    }

    @Override
    protected AgentPrepaymentOrder from(String orderId, Long id) {
        if (orderId != null) {
            return mainDeliverableOrderService.getOrder(Long.parseLong(orderId));
        }
        return mainDeliverableOrderService.getOrder(id);
    }
}
