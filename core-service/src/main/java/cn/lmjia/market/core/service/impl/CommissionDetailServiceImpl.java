package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.entity.deal.OrderCommission;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.repository.deal.CommissionRepository;
import cn.lmjia.market.core.repository.deal.OrderCommissionRepository;
import cn.lmjia.market.core.service.CommissionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommissionDetailServiceImpl implements CommissionDetailService {

    @Autowired
    private OrderCommissionRepository orderCommissionRepository;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @Autowired
    private CommissionRepository commissionRepository;

    @Override
    public List<Commission> findByOrderId(long id) {
        if (id == 0) {
            return null;
        }
        //结果集
        List<Commission> result = new ArrayList<>();
        //根据订单id查询出订单.
        MainOrder order = mainOrderRepository.findOne(id);
        //根据订单查询所有的佣金记录
        List<OrderCommission> orderCommissionList = orderCommissionRepository.findBySource(order);
        //查询这个佣金记录中的详情
        if (orderCommissionList.size() != 0 && orderCommissionList != null) {
            for (OrderCommission orderCommission : orderCommissionList) {
                List<Commission> commissionList = commissionRepository.findByOrderCommission(orderCommission);
                result.addAll(commissionList);
            }
        }
        return result;
    }

    @Override
    @Scheduled(cron = "0 0 9 ? * 2")
    public void sendComissionDetailWeekly() {

    }
}
