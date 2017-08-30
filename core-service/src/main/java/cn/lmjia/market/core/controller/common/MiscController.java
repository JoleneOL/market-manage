package cn.lmjia.market.core.controller.common;

import cn.lmjia.market.core.model.ApiResult;
import cn.lmjia.market.core.service.LoginService;
import com.huotu.verification.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * 杂七杂八的玩意儿
 *
 * @author CJ
 */
@Controller
public class MiscController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private LoginService loginService;

    @RequestMapping(method = RequestMethod.POST, value = "/misc/sendLoginCode")
    @ResponseBody
    public ApiResult sendLoginCode(String mobile) throws IOException {
        verificationCodeService.sendCode(mobile, loginService.loginVerificationType());
        return ApiResult.withOk();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/misc/sendRegisterCode")
    @ResponseBody
    public ApiResult sendRegisterCode(String mobile) throws IOException {
        // 必须确保该用户并不存在 否者注册个球
        if (loginService.byLoginName(mobile) != null) {
            return ApiResult.withCodeAndMessage(401, "该手机号码已被人使用", null);
//            throw new IllegalArgumentException("该手机号码已被人使用");
        }
        verificationCodeService.sendCode(mobile, loginService.registerVerificationType());
        return ApiResult.withOk();
    }

}
