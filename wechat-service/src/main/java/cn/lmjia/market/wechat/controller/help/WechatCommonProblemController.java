package cn.lmjia.market.wechat.controller.help;

import cn.lmjia.market.core.entity.help.CommonProblem;
import cn.lmjia.market.core.service.help.CommonProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public List<CommonProblem> search(@RequestParam("title") String title){
        return commonProblemService.findByTitle(title);
    }

    @GetMapping("/commonProblem")
    public String index(){
        return "wechat@helpCenter/index.html";
    }


    @PutMapping("/help/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void enable(@PathVariable("id") long id){
        CommonProblem commonProblem = commonProblemService.getOne(id);
        commonProblem.setEnable(true);
    }

    @PutMapping("/help/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void disable(@PathVariable("id") long id){
        CommonProblem commonProblem = commonProblemService.getOne(id);
        commonProblem.setEnable(false);
    }

    @PutMapping("/help/{id}/isWeightLabel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void isWeightLabel(@PathVariable("id") long id){
        CommonProblem commonProblem = commonProblemService.getOne(id);
        commonProblem.setWeight(true);
    }

    @PutMapping("/help/{id}/notWeightLabel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void notWeightLabel(@PathVariable("id") long id){
        CommonProblem commonProblem = commonProblemService.getOne(id);
        commonProblem.setWeight(false);
    }
}
