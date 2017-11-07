package cn.lmjia.market.manage.controller.help;

import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.service.help.CommonProblemService;
import cn.lmjia.market.manage.ManageServiceTest;
import cn.lmjia.market.manage.page.ManageHelpCenterPage;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import static org.assertj.core.api.Assertions.assertThat;


public class ManageCommonProblemControllerTest extends ManageServiceTest {

    @Autowired
    CommonProblemService commonProblemService;

    @Test
    public void index(){
        Manager manager = newRandomManager(ManageLevel.root);
        updateAllRunWith(manager);

        String title = RandomStringUtils.randomAscii(10);
        commonProblemService.addAndEditCommonProblem(null, title,50 , RandomStringUtils.randomAscii(20));

        //打开页面
        driver.get("http://localhost/manage/commonProblem");

        ManageHelpCenterPage manageHelpCenterPage = initPage(ManageHelpCenterPage.class);

        manageHelpCenterPage.assertHasTopic(title);
    }

}
