package cn.lmjia.market.core.service;

/**
 * 系统服务；它不依赖任何玩意儿
 *
 * @author CJ
 */
public interface SystemService {

    /**
     * 我的URI
     */
    String wechatMyURi = "/wechatMy";
    /**
     * 我的团队URI
     */
    String wechatMyTeamURi = "/wechatMyTeam";
    /**
     * 下单URI
     */
    String wechatOrderURi = "/wechatOrder";

    /**
     * @param uri 传入uri通常/开头
     * @return 完整路径
     */
    String toUrl(String uri);
}
