package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * 初始化服务
 *
 * @author CJ
 */
@Service
public class InitService {

    @Autowired
    private LoginService loginService;

    @PostConstruct
    @Transactional
    public void init() {
        managers();
    }

    private void managers() {
        if (loginService.managers().isEmpty()) {
            // 添加一个主管理员
            Manager manager = new Manager();
            manager.setLevel(ManageLevel.root);
            manager.setLoginName("root");
            loginService.password(manager, "rootIsRoot");
        }
    }
}
