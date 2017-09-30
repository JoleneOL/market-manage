package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.entity.deal.OrderCommission;
import cn.lmjia.market.core.repository.deal.CommissionRepository;
import cn.lmjia.market.core.repository.deal.OrderCommissionRepository;
import cn.lmjia.market.dealer.service.CommissionRateService;
import cn.lmjia.market.dealer.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommissionServiceImpl implements CommissionService {

    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired
    private OrderCommissionRepository orderCommissionRepository;

    @Override
    public List<Commission> findByOrderId(long id) {
        if (id == 0) {
            return null;
        }
        //获取该订单所有的佣金记录
        List<OrderCommission> ocResult = orderCommissionRepository.findBySource(id);
        //返回结果集
        List<Commission> result = new ArrayList<>();

        if (ocResult.size() != 0) {
            for (OrderCommission orderCommission : ocResult) {
                //获取关于这个佣金记录中某个用户从中获取的佣金收益
                List<Commission> commissionList = commissionRepository.findByOrderCommission(orderCommission);
                result.addAll(commissionList);
            }
        }
        return result;
    }
}
