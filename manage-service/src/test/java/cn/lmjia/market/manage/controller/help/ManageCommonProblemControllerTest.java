package cn.lmjia.market.manage.controller.help;

import cn.lmjia.market.core.entity.help.CommonProblem;
import cn.lmjia.market.core.service.help.CommonProblemService;
import cn.lmjia.market.manage.ManageServiceTest;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import static org.assertj.core.api.Assertions.assertThat;


public class ManageCommonProblemControllerTest extends ManageServiceTest {

    @Autowired
    CommonProblemService commonProblemService;

    @Test
    public void go1(){
        go(null,"常见问题","测试常见问题是否好使.");
    }

    public void go(Long id,String title,String content){
        if(StringUtils.isBlank(title)){
            throw new IllegalArgumentException("");
        }
        CommonProblem commonProblem = commonProblemService.addCommonProblem(id, title, content);
        assertThat(commonProblem)
                .isNotNull();
    }

}
