package cn.lmjia.market.wechat.controller.help;

import cn.lmjia.market.core.entity.help.CommonProblem;
import cn.lmjia.market.core.service.help.CommonProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author lxf
 */
@Controller
public class WechatCommonProblemController {
    @Autowired
    CommonProblemService commonProblemService;

    @GetMapping("/commonProblemDetail/{id}")
    public String commonProblemDetail(@PathVariable Long id, Model model){
        model.addAttribute("commonProblem",commonProblemService.getOne(id));
        return "wechat@helpCenter/helpDetail.html";
    }

    @GetMapping("/commonProblem/search")
    @ResponseBody
    public List<CommonProblem> search(String title){
        return commonProblemService.findByTitle(title);
    }

    @GetMapping("/commonProblem")
    public String index(){
        return "wechat@helpCenter/index.html";
    }
}
