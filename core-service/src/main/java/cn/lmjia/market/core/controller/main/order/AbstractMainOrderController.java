package cn.lmjia.market.core.controller.main.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.MainGoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

/**
 * @author CJ
 */
public abstract class AbstractMainOrderController {
    @Autowired
    private MainGoodRepository mainGoodRepository;

    /**
     * 展示下单页面
     *
     * @param login 当前身份
     * @param model model
     */
    protected void orderIndex(Login login, Model model) {
        model.addAttribute("goodList", mainGoodRepository.findByEnableTrue());
    }
}
