package cn.lmjia.market.wechat.controller.help;

import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.service.help.CommonProblemService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.HelpCenterPage;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class WechatCommonProblemControllerTest extends WechatTestBase{

    @Autowired
    private CommonProblemService commonProblemService;

    @Test
    public void index() throws Exception {

//        Login login = newRandomLogin();
//        updateAllRunWith(login);
        String title = RandomStringUtils.randomAscii(10);
        commonProblemService.addAndEditCommonProblem(null, title,50 , RandomStringUtils.randomAscii(20));

        //打开页面
        driver.get("http://localhost"+ SystemService.helpCenterURi);

        HelpCenterPage page = initPage(HelpCenterPage.class);
        //在帮助首页查看是否有这个标题的帮助
        page.assertHasTopic(title);
        //点击进入详情页面
        HelpDetailPage helpDetailPage = page.clickHelpDetail();

        //判断是否有这个标题的帮助
        helpDetailPage.asssertHasTopic(title);
    }

}