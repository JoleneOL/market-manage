package cn.lmjia.market.core.service;

import cn.lmjia.market.core.Version;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import me.jiangcai.lib.upgrade.VersionUpgrade;
import me.jiangcai.lib.upgrade.service.UpgradeService;
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
    @Autowired
    private UpgradeService upgradeService;

    @PostConstruct
    @Transactional
    public void init() {
        upgrade();
        managers();
    }

    private void upgrade() {
        //noinspection Convert2Lambda
        upgradeService.systemUpgrade(new VersionUpgrade<Version>() {
            @Override
            public void upgradeToVersion(Version version) throws Exception {
                switch (version) {
                    case init:
                        break;
                }

            }
        });
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
