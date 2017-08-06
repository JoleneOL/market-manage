package cn.lmjia.market.wechat.service;

import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.trj.TRJEnhanceConfig;
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

    @PostConstruct
    public void init() {
        //菜单
        if (environment.acceptsProfiles(CoreConfig.ProfileUnitTest)) {
            log.info("单元测试时没有必要更新公众号菜单");
            return;
        }
        Protocol.forAccount(publicAccount).createMenu(
                new Menu[]
                        {
                                createMenu("推广", systemService.toUrl(SystemService.wechatShareUri))
                                , createMenu("下单"
                                , createMenu("购买", systemService.toUrl(SystemService.wechatOrderURi))
                                , createMenu("分期", systemService.toUrl(TRJEnhanceConfig.TRJOrderURI)))
                                , createMenu("我的", systemService.toUrl(SystemService.wechatMyURi))
                        }
        );
        log.info("updated the menus");
    }

    private Menu createMenu(String name, Menu... menus) {
        Menu menu = new Menu();
        menu.setType(MenuType.parent);
        menu.setName(name);
        menu.setSubs(menus);
        return menu;
    }

    private Menu createMenu(String name, String url) {
        Menu menu = new Menu();
        menu.setType(MenuType.view);
        menu.setName(name);
        menu.setData(url);
        return menu;
    }


}
