package cn.lmjia.market.wechat.service;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.trj.TRJEnhanceConfig;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.wx.model.Menu;
import me.jiangcai.wx.model.MenuType;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.protocol.Protocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Service
public class WechatInitService {

    private static final Log log = LogFactory.getLog(WechatInitService.class);

    @Autowired
    private SystemService systemService;
    @Autowired
    private PublicAccount publicAccount;
    @Autowired
    private Environment environment;
    @Autowired
    private SystemStringService systemStringService;

    @PostConstruct
    public void init() {
        //菜单
        if (environment.acceptsProfiles(CoreConfig.ProfileUnitTest)) {
            log.info("单元测试时没有必要更新公众号菜单");
            return;
        }
        try {
            Protocol.forAccount(publicAccount).createMenu(
                    new Menu[]
                            {
                                    createMenu("推广", systemService.toUrl(SystemService.wechatShareUri))
                                    , createMenu("下单"
                                    , systemStringService.getCustomSystemString("market.mall.wechat.menu.on"
                                            , null, false, Boolean.class, false)
                                            ? createMenu("商城", systemService.toUrl("/wechatIndex")) : null
                                    , createMenu("购买", systemService.toUrl(SystemService.wechatOrderURi))
                                    , createMenu("花呗分期", systemService.toUrl(SystemService.wechatOrderURiHB))
                                    , createMenu("投融家分期", systemService.toUrl(TRJEnhanceConfig.TRJOrderURI))
                            )
//                                    ,createMenu("下单", systemService.toUrl(SystemService.wechatOrderURi))
                                    , createMenu("我的", systemService.toUrl(SystemService.wechatMyURi))
                            }
            );
            log.info("updated the menus");
        } catch (Throwable ex) {
            log.warn("Error on Update Wechat Menus", ex);
        }

    }

    private Menu createMenu(String name, Menu... menus) {
        // 把 null 移除掉
        Menu menu = new Menu();
        menu.setType(MenuType.parent);
        menu.setName(name);
        menu.setSubs(Stream.of(menus)
                .filter(Objects::nonNull)
                .toArray(Menu[]::new)
        );
        return menu;
    }

    private Menu createMenu(String name, String url) {
        if ("投融家分期".equals(name)
                && !systemStringService.getCustomSystemString("market.trj.enable", null
                , false, Boolean.class, false)) {
            return null;
        }
        Menu menu = new Menu();
        menu.setType(MenuType.view);
        menu.setName(name);
        menu.setData(url);
        return menu;
    }


}
