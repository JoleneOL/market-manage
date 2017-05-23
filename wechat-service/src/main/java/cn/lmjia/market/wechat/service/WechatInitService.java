package cn.lmjia.market.wechat.service;

import cn.lmjia.market.core.service.SystemService;
import me.jiangcai.wx.model.Menu;
import me.jiangcai.wx.model.MenuType;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.protocol.Protocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostConstruct
    public void init() {
        //菜单
        Protocol.forAccount(publicAccount).createMenu(
                new Menu[]
                        {
                                createMenu("我的团队", systemService.toUrl(SystemService.wechatMyTeamURi))
                                , createMenu("下单", systemService.toUrl(SystemService.wechatOrderURi))
                                , createMenu("我的", systemService.toUrl(SystemService.wechatMyURi))
                        }
        );
        log.info("updated the menus");
    }

    private Menu createMenu(String name, String url) {
        Menu menu = new Menu();
        menu.setType(MenuType.view);
        menu.setName(name);
        menu.setData(url);
        return menu;
    }


}
