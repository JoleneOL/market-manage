package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.deal.Commission;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 佣金详情服务
 *
 * @author lxf
 */
public interface CommissionDetailService {

    /**
     * 根据OrderId去查找该订单产生的每一笔佣金
     * @param id  OrderId
     * @return 这笔订单每一笔佣金的list集合
     */
    List<Commission> findByOrderId(long id);

    /**
     * 每周一发送周报,将上周获得佣金金额总数发送给获取者.
     */
    @Scheduled(cron = "0 0 9 ? * 2")
    @Transactional
    void sendComissionDetailWeekly();

}
