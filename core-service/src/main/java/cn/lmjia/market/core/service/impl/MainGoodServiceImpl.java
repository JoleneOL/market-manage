package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.service.MainGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CJ
 */
@Service("mainGoodService")
public class MainGoodServiceImpl implements MainGoodService {
    @Autowired
    private MainGoodRepository mainGoodRepository;

    @Override
    public List<MainGood> forSale() {
        return mainGoodRepository.findByEnableTrue();
    }
}
