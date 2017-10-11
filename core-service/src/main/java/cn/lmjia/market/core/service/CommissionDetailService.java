package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.deal.Commission;

import java.util.List;

/**
 * 佣金详情服务
 *
 * @author lxf
 */
public interface CommissionDetailService {

    List<Commission> findByOrderId(long id);
}
