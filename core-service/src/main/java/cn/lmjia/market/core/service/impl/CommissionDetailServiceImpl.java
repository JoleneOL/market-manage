package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.define.MarketNoticeType;
import cn.lmjia.market.core.define.MarketUserNoticeType;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.entity.deal.OrderCommission;
import cn.lmjia.market.core.entity.deal.OrderCommission_;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.repository.deal.CommissionRepository;
import cn.lmjia.market.core.repository.deal.OrderCommissionRepository;
import cn.lmjia.market.core.service.CommissionDetailService;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.WechatNoticeHelper;
import me.jiangcai.user.notice.UserNoticeService;
import me.jiangcai.wx.model.message.SimpleTemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CommissionDetailServiceImpl implements CommissionDetailService {

    @Autowired
    private OrderCommissionRepository orderCommissionRepository;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired
    private WechatNoticeHelper wechatNoticeHelper;
    @Autowired
    private UserNoticeService userNoticeService;
    @Autowired
    private LoginService loginService;

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
        String start = LocalDate.now()+" 24:00:00";//获取今天日期
        LocalDateTime endTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime startTime = endTime.minusDays(7);//七天前的日期
        //所有的当周获取过佣金的人.
        Specification<OrderCommission> querySpecifi = new Specification<OrderCommission>(){

            @Override
            public Predicate toPredicate(Root<OrderCommission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.greaterThanOrEqualTo(root.get(OrderCommission_.generateTime),startTime));
                predicates.add(cb.lessThanOrEqualTo(root.get(OrderCommission_.generateTime), endTime));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        //查询最近一周所有的订单佣金记录
        List<OrderCommission> weekOrderCommission = orderCommissionRepository.findAll(querySpecifi);
        List<Commission> commissionList = new ArrayList<>();
        Set<Login> loginSet = new HashSet<>();
        //获取到所有的佣金详情.
        for (OrderCommission orderCommission : weekOrderCommission) {
            List<Commission> byOrderCommission = commissionRepository.findByOrderCommission(orderCommission);
            commissionList.addAll(byOrderCommission);
        }
        for (Commission commission : commissionList) {
            loginSet.add(commission.getWho());
        }

        Map<Login,BigDecimal> loginAndAmount = new HashMap<>();
        for (Commission commission : commissionList) {
            for (Login login : loginSet) {
                loginAndAmount.put(login,new BigDecimal(0));
                if(commission.getWho() == login){
                    loginAndAmount.put(login,loginAndAmount.get(login).add(commission.getAmount()));
                }
            }
        }
        CommissionWeekly commissionWeekly = new CommissionWeekly();
        //写一个网页
        wechatNoticeHelper.registerTemplateMessage(commissionWeekly,"/wechatCommissionWeekly");
        for (Login login : loginAndAmount.keySet()) {
            userNoticeService.sendMessage(null, loginService.toWechatUser(Collections.singleton(login.getGuideUser()))
                    ,null, commissionWeekly, "￥" + loginAndAmount.get(login).toString(),startTime.toLocalDate()+"-"+endTime.toLocalDate());
        }


    }

    /**
     * 佣金周报通知.
     */
    private class CommissionWeekly implements MarketUserNoticeType {

        @Override
        public Collection<? extends TemplateMessageParameter> parameterStyles() {
            return Arrays.asList(
                    new SimpleTemplateMessageParameter("first", "上周佣金周报。")
                    , new SimpleTemplateMessageParameter("keyword1", "{0}")
                    , new SimpleTemplateMessageParameter("keyword2", "{1}")
                    , new SimpleTemplateMessageParameter("remark", "感谢您的使用。")
            );
        }

        @Override
        public MarketNoticeType type() {
            return MarketNoticeType.CommissionWeekly;
        }

        @Override
        public String title() {
            return "佣金周报";
        }

        @Override
        public boolean allowDifferentiation() {
            return true;
        }

        @Override
        public String defaultToText(Locale locale, Object[] parameters) {
            return "佣金周报";
        }

        @Override
        public String defaultToHTML(Locale locale, Object[] parameters) {
            return "佣金周报";
        }

        @Override
        public Class<?>[] expectedParameterTypes() {
            return new Class<?>[]{
                    String.class,// 0时间段.
                    String.class, // 1佣金金额.
            };
        }
    }
}
