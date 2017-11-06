package cn.lmjia.market.wechat.controller.help;

import cn.lmjia.market.core.entity.help.CommonProblem;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.service.help.CommonProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> search(String title){
        Map<String, Object> result = new HashMap<>();
        try{
            List<CommonProblem> problemList = commonProblemService.findByTitle(title);
            result.put("data",problemList);
            result.put("resultCode",200);
            result.put("resultMsg","OK");
        }catch(Exception e){
            result.put("resultCode",500);
            result.put("resultMsg","error");
        }
        return result;
    }

    @GetMapping(SystemService.helpCenterURi)
    public String index(){
        return "wechat@helpCenter/index.html";
    }


}
