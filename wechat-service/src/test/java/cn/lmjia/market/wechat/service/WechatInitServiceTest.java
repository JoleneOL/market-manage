package cn.lmjia.market.wechat.service;

import cn.lmjia.market.wechat.WechatTestBase;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CJ
 */
public class WechatInitServiceTest extends WechatTestBase {

    //    企业介绍 利每家企业介绍
//    企业视频 利每家企业宣传片
//            招商加盟 利每家招商加盟
    @Autowired
    private SystemStringService systemStringService;

    @Test
    public void go() {
        // 参数调整之后可以接受到事件
        systemStringService.updateSystemString("market.first.menus", "企业介绍:利每家企业介绍|企业视频:利每家企业宣传片|招商加盟:利每家招商加盟");
    }

}