package cn.lmjia.market.core.controller.equipment;

import cn.lmjia.market.core.model.ApiResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;

/**
 * @author CJ
 */
@Controller
public class EquipmentDataController {
    @RequestMapping(method = RequestMethod.GET, value = "/api/equipmentList")
    @ResponseBody
    public ApiResult equipmentList() {
        return ApiResult.withOk(Collections.emptyList());
    }
}
