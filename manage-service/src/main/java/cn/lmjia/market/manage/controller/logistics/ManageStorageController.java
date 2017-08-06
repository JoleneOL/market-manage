package cn.lmjia.market.manage.controller.logistics;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT')")
public class ManageStorageController {

    // 库存 为0 的信息
    // 库存 小于警戒线的信息
    // 每一种货品的 所有库存信息

    @GetMapping("/manageStorage")
    public String index() {
        return "_storageManage.html";
    }

    @GetMapping("/manageStorageDelivery")
    public String delivery() {
        return "_delivery.html";
    }

}
