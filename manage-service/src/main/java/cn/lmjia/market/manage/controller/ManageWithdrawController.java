package cn.lmjia.market.manage.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/**
 * 管理提现
 *
 * @author CJ
 */
@PreAuthorize("hasRole('ROOT')")
@Controller
public class ManageWithdrawController {


}
