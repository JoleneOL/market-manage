package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理员相关服务
 *
 * @author CJ
 */
public interface ManagerService {

    /**
     * @param manageLevel 特定管理级别
     * @return 拥有特定管理级别的用户
     */
    @Transactional(readOnly = true)
    List<Manager> levelAs(ManageLevel manageLevel);

}
