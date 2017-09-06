package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.Salesman;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.repository.deal.SalesmanRepository;
import cn.lmjia.market.core.service.SalesmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class SalesmanServiceImpl implements SalesmanService {

    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private SalesmanRepository salesmanRepository;

    @Override
    public void salesmanShareTo(long salesmanId, Login login) {
    }

    @Override
    public Salesman get(long id) {
        return salesmanRepository.getOne(loginRepository.getOne(id));
    }
}
