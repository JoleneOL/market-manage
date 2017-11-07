package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentRate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统服务；它不依赖任何玩意儿
 *
 * @author CJ
 */
public interface SystemService {
    /**
     * 预付货款批货列表
     */
    String goodAdvanceOrderList = "/wechatGoodAdvanceOrderList";
    /**
     * 显示业绩的URL
     */
    String wechatSales = "/wechatSales";
    /**
     * 我的URI
     */
    String wechatMyURi = "/wechatMy";
    /**
     * 推广URI
     */
    String wechatShareUri = "/wechatShare";
    /**
     * 推广URI，更多（賺钱计划）
     */
    String wechatShareMoreUri = "/wechatShareMore";
    /**
     * 我的团队URI
     */
    String wechatMyTeamURi = "/wechatMyTeam";
    /**
     * 下单URI
     */
    String wechatOrderURi = "/wechatOrder";
    /**
     * 支付URI
     */
    String wechatPayOrderURi = "/wechatOrderPay";
    /**
     * 下单URI
     */
    String wechatOrderURiHB = "/wechatOrderHB";
    /**
     * 商城下单URI
     */
    String mallOrderURi = "/mallOrder";
    String wechatMallIndex = "/wechatIndex";

    /**
     * @return 代理体系的层次数量
     */
    default int systemLevel() {
        return 5;
    }

    /**
     * @return 默认代理体系的代理奖励层次
     */
    default Map<Integer, AgentRate> defaultAgentRates() {
        Map<Integer, AgentRate> data = new HashMap<>();
        data.put(0, new AgentRate(BigDecimal.ZERO, BigDecimal.ZERO));
        data.put(1, new AgentRate(BigDecimal.ZERO, BigDecimal.ZERO));
        data.put(2, new AgentRate(new BigDecimal("0.05"), new BigDecimal("0.01")));
        data.put(3, new AgentRate(new BigDecimal("0.05"), new BigDecimal("0.01")));
        // 暂时取消 level 3的默认分成
//        data.put(3, new AgentRate(BigDecimal.ZERO, BigDecimal.ZERO));
        data.put(4, new AgentRate(new BigDecimal("0.05"), new BigDecimal("0.01")));
        return data;
    }

    /**
     * @return 默认代理体系的区域奖励
     */
    default BigDecimal defaultAddressRate() {
        return new BigDecimal("0.02");
    }

    /**
     * @return 默认代理体系的直销奖励
     */
    default BigDecimal defaultOrderRate() {
        return new BigDecimal("0.2");
    }

    /**
     * @param uri 传入uri通常/开头
     * @return 完整路径
     */
    String toUrl(String uri);

    /**
     * @return 区域奖励针对的等级
     */
    default int addressRateForLevel() {
        return 2;
    }

    /**
     * @return 公司客服电话
     */
    String getCompanyCustomerServiceTel();

    /**
     * @param login 当前登录的用户
     * @return 是否显示提现按钮
     */
    boolean allowWithdrawDisplay(Login login);

    /**
     * @param value 非代理商是否拥有获得销售奖励的资格
     */
    void updateNonAgentAbleToGainCommission(boolean value);

    /**
     * @return 非代理商是否拥有获得销售奖励的资格
     */
    boolean isNonAgentAbleToGainCommission();

    /**
     * @param value 爱心天使的认定是否只需要完成一笔任意订单
     */
    void updateRegularLoginAsAnyOrder(boolean value);

    /**
     * @return 爱心天使的认定是否只需要完成一笔任意订单
     */
    boolean isRegularLoginAsAnyOrder();

    /**
     * @return 爱心天使的认定是否需要完成一笔足够金额的订单;如果null 表示没有这个条件
     */
    BigDecimal getRegularLoginAsSingleOrderAmount();

    /**
     * @return 爱心天使的认定是否需要累计完成足够金额的订单;如果null 表示没有这个条件
     */
    BigDecimal getRegularLoginAsTotalOrderAmount();

    /**
     * @return 爱心天使的认定是否需要在一天内累计完成足够金额的订单;如果null 表示没有这个条件
     */
    BigDecimal getRegularLoginAs24HOrderAmount();

    default String getFirstName() {
        return "利每家";
    }
}
