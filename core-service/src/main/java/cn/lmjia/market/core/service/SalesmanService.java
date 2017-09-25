package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.SalesAchievement;
import cn.lmjia.market.core.entity.deal.Salesman;
import cn.lmjia.market.core.row.RowDefinition;
import me.jiangcai.wx.MessageReply;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 销售人员计划
 *
 * @author CJ
 */
public interface SalesmanService extends MessageReply {
    /**
     * 销售人员salesmanId刚刚推荐了login
     *
     * @param salesmanId
     * @param login
     */
    @Transactional
    void salesmanShareTo(long salesmanId, Login login);

    /**
     * 用户试图下单，获取一个业绩
     *
     * @param login 用户
     * @return 如果并不存在则返回null
     */
    @Transactional
    SalesAchievement pick(Login login);

    /**
     * @param salesman 销售
     * @return 该销售所有的绩效
     */
    @Transactional(readOnly = true)
    List<SalesAchievement> all(Salesman salesman);

    @Transactional(readOnly = true)
    Salesman get(long id);

    @Transactional
    Salesman newSalesman(Login login, BigDecimal rate, String rank);

    @Transactional(readOnly = true)
    SalesAchievement getAchievement(long id);

    /**
     * @param login  查询的身份
     * @param date   可选的特定时间
     * @param remark 可选的是否备注
     * @param deal   可选的是否已成交
     * @return 搜索数据的定义
     */
    RowDefinition<SalesAchievement> data(Login login, LocalDate date, Boolean remark, Boolean deal);
}
