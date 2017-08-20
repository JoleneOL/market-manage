package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.ManagerRepository;
import cn.lmjia.market.core.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CJ
 */
@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Override
    public List<Manager> levelAs(ManageLevel manageLevel) {
        return managerRepository.findAll((root, query, cb) -> cb.isMember(manageLevel, root.get("levelSet")));
    }
}
