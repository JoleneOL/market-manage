package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.define.MarketNoticeType;
import cn.lmjia.market.core.define.MarketUserNoticeType;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Customer_;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Login_;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentLevel_;
import cn.lmjia.market.core.entity.deal.Salesman;
import cn.lmjia.market.core.entity.deal.Salesman_;
import cn.lmjia.market.core.repository.ContactWayRepository;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.repository.ManagerRepository;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.service.LoginService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.core.service.WechatNoticeHelper;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.service.VerificationCodeService;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.user.notice.NoticeChannel;
import me.jiangcai.user.notice.User;
import me.jiangcai.user.notice.UserNoticeService;
import me.jiangcai.user.notice.wechat.WechatNoticeChannel;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.model.message.SimpleTemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import me.jiangcai.wx.standard.entity.StandardWeixinUser;
import me.jiangcai.wx.standard.repository.StandardWeixinUserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static final Log log = LogFactory.getLog(LoginServiceImpl.class);
    /**
     * 负责干这个事儿的，5线程应该是足够了
     */
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private ContactWayRepository contactWayRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StandardWeixinUserRepository standardWeixinUserRepository;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SystemStringService systemStringService;
    @Autowired
    private ReadService readService;
    @Autowired
    private WechatNoticeHelper wechatNoticeHelper;
    @Autowired
    private UserNoticeService userNoticeService;
    @Autowired
    private SystemService systemService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Login login = loginRepository.findByLoginName(username);
        if (login == null)
            throw new UsernameNotFoundException(username);
        return login;
    }

    @Override
    public List<Manager> managers() {
        return managerRepository.findAll();
    }

    @Override
    public <T extends Login> T password(T login, String loginName, String rawPassword) {
        if (loginName != null)
            login.setLoginName(loginName);
        login.setPassword(passwordEncoder.encode(rawPassword));
        return loginRepository.save(login);
    }

    @Override
    public Login get(long id) {
        return loginRepository.getOne(id);
    }

    @Override
    public <T extends Login> T newLogin(Class<T> type, String username, Login guide, String rawPassword) {
        T login = newLogin(type, guide);
        return password(login, username, rawPassword);
    }

    private <T extends Login> T newLogin(Class<T> type, Login guide) {
        T login;
        try {
            login = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        login.setCreatedTime(LocalDateTime.now());
        login.setGuideUser(guide);
        return login;
    }

    @Override
    public <T extends Login> T newLogin(Class<T> type, Login guide, StandardWeixinUser weixinUser) {
        T login = newLogin(type, guide);
        login.setWechatUser(weixinUser);
        return loginRepository.save(login);
    }

    @Override
    public boolean mobileValidation(String mobile) {
        if (loginRepository.count((root, query, cb) -> cb.equal(root.get("loginName"), mobile)) > 0)
            return false;
        if (contactWayRepository.count((root, query, cb) -> cb.equal(root.get("mobile"), mobile)) > 0)
            return false;
        if (log.isTraceEnabled())
            log.trace("通过手机可用性检测:" + mobile);
        return true;
    }

    @Override
    public Login asWechat(String openId) {
        return loginRepository.findOne((root, query, cb)
                -> cb.and(cb.equal(root.get("wechatUser").get("openId"), openId)
                , cb.notEqual(root.type(), Manager.class))
        );
    }

    @Override
    public void bindWechat(String loginName, String rawPassword, String openId) {
        Login login = getEnableLogin(loginName);
        if (!passwordEncoder.matches(rawPassword, login.getPassword())) {
            throw new IllegalArgumentException();
        }

        login.setWechatUser(standardWeixinUserRepository.findByOpenId(openId));
    }

    private Login getEnableLogin(String loginName) {
        Login login = loginRepository.findByLoginName(loginName);
        if (login == null || !login.isEnabled()
                || !login.isAccountNonExpired()
                || !login.isAccountNonLocked()
                || !login.isCredentialsNonExpired())
            throw new IllegalArgumentException();
        return login;
    }

    @Override
    public void bindWechatWithCode(String mobile, String code, String openId) throws IllegalVerificationCodeException {
        verificationCodeService.verify(mobile, code, loginVerificationType());
        Login login = getEnableLogin(mobile);
        login.setWechatUser(standardWeixinUserRepository.findByOpenId(openId));
    }

    @Override
    public AgentLevel lowestAgentLevel(Login who) {
        if (who == null)
            return null;
        List<AgentLevel> allAgent = agentLevelRepository.findByLogin(who);

        if (allAgent.isEmpty()) {
//            List<Customer> customers = customerRepository.findByLogin(who);
//            if (customers.isEmpty())
            return lowestAgentLevel(who.getGuideUser());
//            return customers.get(0).getAgentLevel();
//            return lowestAgentLevel(who.getGuideUser());
        }

        // 排除掉所有
        AgentLevel[] all = new AgentLevel[allAgent.size()];
        allAgent.toArray(all);

        for (AgentLevel agentLevel : all) {
            // 有人以agentLevel为上级?
            allAgent.stream()
                    .filter(level
                            -> level.getSuperior() == agentLevel)
                    .findAny()
                    .ifPresent(level
                            -> allAgent.remove(agentLevel));
        }

        return allAgent.get(0);
    }

    @Override
    public Login byLoginName(String loginName) {
        return loginRepository.findByLoginName(loginName);
    }

    @Override
    public boolean isManager(String loginName) {
        Login login = byLoginName(loginName);
        return login != null && login.isManageable();
    }

    @Override
    public boolean isRegularLogin(Login login, MainOrder order) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // 订单是否存在
        if (systemService.isRegularLoginAsAnyOrder()) {
            log.trace("身份认定只需任意订单即可 for:" + login);
            if (order != null)
                return true;
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<MainOrder> root = criteriaQuery.from(MainOrder.class);
            try {
                return entityManager.createQuery(criteriaQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(MainOrder_.orderBy), login)
                        , criteriaBuilder.greaterThan(root.get(MainOrder_.goodCommissioningPriceAmountIndependent)
                                , BigDecimal.ZERO)
                        , MainOrder.getOrderPaySuccess(root, criteriaBuilder)
                ))
                        .select(criteriaBuilder.count(root)))
                        .getSingleResult() > 0;
            } catch (NoResultException ignored) {
                return false;
            }
        }
        // 条件应该是从宽松到严格！
        // 爱心天使的认定是否需要累计完成足够金额的订单
        final BigDecimal regularLoginAsTotalOrderAmount = systemService.getRegularLoginAsTotalOrderAmount();
        if (regularLoginAsTotalOrderAmount != null) {
            log.trace("身份认定需累计下单超过金额:" + regularLoginAsTotalOrderAmount + " for:" + login);
            CriteriaQuery<BigDecimal> criteriaQuery = criteriaBuilder.createQuery(BigDecimal.class);
            Root<MainOrder> root = criteriaQuery.from(MainOrder.class);
            try {
                return entityManager.createQuery(criteriaQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(MainOrder_.orderBy), login)
                        , criteriaBuilder.greaterThan(root.get(MainOrder_.goodCommissioningPriceAmountIndependent)
                                , BigDecimal.ZERO)
                        , MainOrder.getOrderPaySuccess(root, criteriaBuilder)
                ))
                        .select(criteriaBuilder.sum(root.get(MainOrder_.goodCommissioningPriceAmountIndependent))))
                        .getSingleResult().compareTo(regularLoginAsTotalOrderAmount) != -1;
            } catch (NoResultException ignored) {
                return false;
            }
        }

        // 爱心天使的认定是否需要在一天内累计完成足够金额的订单
        final BigDecimal regularLoginAs24HOrderAmount = systemService.getRegularLoginAs24HOrderAmount();
        if (regularLoginAs24HOrderAmount != null) {
            log.trace("身份认定需单日累计下单超过金额:" + regularLoginAs24HOrderAmount + " for:" + login);
            LocalDateTime endTime;
            if (order != null) {
                endTime = order.getOrderTime();
            } else
                endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusDays(1);
            CriteriaQuery<BigDecimal> criteriaQuery = criteriaBuilder.createQuery(BigDecimal.class);
            Root<MainOrder> root = criteriaQuery.from(MainOrder.class);
            try {
                return entityManager.createQuery(criteriaQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(MainOrder_.orderBy), login)
                        , criteriaBuilder.greaterThan(root.get(MainOrder_.goodCommissioningPriceAmountIndependent)
                                , BigDecimal.ZERO)
                        , MainOrder.getOrderPaySuccess(root, criteriaBuilder)
                        , criteriaBuilder.greaterThanOrEqualTo(root.get(MainOrder_.orderTime), startTime)
                        , criteriaBuilder.lessThanOrEqualTo(root.get(MainOrder_.orderTime), endTime)
                ))
                        .select(criteriaBuilder.sum(root.get(MainOrder_.goodCommissioningPriceAmountIndependent))))
                        .getSingleResult().compareTo(regularLoginAs24HOrderAmount) != -1;
            } catch (NoResultException ignored) {
                return false;
            }
        }


        // 爱心天使的认定是否需要完成一笔足够金额的订单
        final BigDecimal regularLoginAsSingleOrderAmount = systemService.getRegularLoginAsSingleOrderAmount();
        if (regularLoginAsSingleOrderAmount != null) {
            log.trace("身份认定需累计下单超过金额:" + regularLoginAsSingleOrderAmount + " for:" + login);
            if (order != null && order.getCommissioningAmount().compareTo(regularLoginAsSingleOrderAmount) == 1)
                return true;
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<MainOrder> root = criteriaQuery.from(MainOrder.class);
            try {
                return entityManager.createQuery(criteriaQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(MainOrder_.orderBy), login)
                        , criteriaBuilder.greaterThan(root.get(MainOrder_.goodCommissioningPriceAmountIndependent)
                                , BigDecimal.ZERO)
                        , MainOrder.getOrderPaySuccess(root, criteriaBuilder)
                        , criteriaBuilder.greaterThanOrEqualTo(root.get(MainOrder_.goodCommissioningPriceAmountIndependent), regularLoginAsSingleOrderAmount)
                ))
                        .select(criteriaBuilder.count(root)))
                        .getSingleResult() > 0;
            } catch (NoResultException ignored) {
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean isRegularLogin(Login login) {
        if (login == null)
            return false;
        if (agentLevelRepository.countByLogin(login) > 0)
            return true;
        // 看下是否为已支付用户
//        if (customerRepository.countByLoginAndSuccessOrderTrue(login) > 0)
//            return true;

        if (!systemService.isNonAgentAbleToGainCommission())
            // 若非代理商没有拥有获得销售奖励的资格，则直接取消
            return false;

        if (login.isSuccessOrder())
            return true;
        return isRegularLogin(login, null);
    }

    @Override
    public void unbindWechat(String loginName) {
        if (loginName == null)
            throw new IllegalStateException("未设置登录名的帐号是无法解绑微信的");
        byLoginName(loginName).setWechatUser(null);
    }

    @Override
    public Collection<User> toWechatUser(Collection<? extends Login> input) {
        return input.stream().filter(login -> login.getWechatUser() != null)
                .map(login -> toUser(login.getWechatUser()))
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    @Override
    public void tryAutoDeleteLogin() {
        log.debug("准备检测是否需要删除");
        final Long timeToDelete = systemStringService.getCustomSystemString("market.autoDelete.minutes", null
                , true, Long.class, 30L);
        final Long timeToWarn = systemStringService.getCustomSystemString("market.autoDeleteWarn.minutes", null
                , true, Long.class, 5L);

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Login> cq = cb.createQuery(Login.class);
        Root<Login> root = cq.from(Login.class);
        // 它也没有推荐给任何人过
        Subquery<Long> teamNumber = cq.subquery(Long.class);
        Root<Login> teamRoot = teamNumber.from(Login.class);
        teamNumber = teamNumber.select(cb.count(teamRoot))
                .where(cb.equal(teamRoot.get(Login_.guideUser), root));
        // 代理商所有的身份
        Subquery<Long> agentLogin = cq.subquery(Long.class);
        Root<AgentLevel> agentRoot = agentLogin.from(AgentLevel.class);
        agentLogin = agentLogin.select(agentRoot.get(AgentLevel_.login).get(Login_.id))
                .distinct(true);
        // 顺手把客户生成的身份也给过滤掉
        Subquery<Long> customerLogin = cq.subquery(Long.class);
        Root<Customer> customerRoot = customerLogin.from(Customer.class);
        customerLogin = customerLogin.select(customerRoot.get(Customer_.login).get(Login_.id))
                .distinct(true)
                .where(cb.isNotNull(customerRoot.get(Customer_.login)));
        //
        Subquery<Long> salesman = cq.subquery(Long.class);
        Root<Salesman> salesmanRoot = salesman.from(Salesman.class);
        salesman = salesman.select(cb.count(salesmanRoot))
                .where(cb.equal(salesmanRoot.get(Salesman_.id), root.get(Login_.id)));
        entityManager.createQuery(cq
                .distinct(true)
                .where(
                        // 非管理员
                        cb.notEqual(root.type(), Manager.class)
                        // 非代理商
                        , root.get(Login_.id).in(agentLogin).not()
                        // 非客户
                        , root.get(Login_.id).in(customerLogin).not()
                        // 团队数量等于0
                        , cb.equal(teamNumber, 0)
                        // 未曾下单
                        , cb.isFalse(root.get(Login_.successOrder))
                        // 而且也非销售人员
                        , cb.equal(salesman, 0L)
                )
        )
                .getResultList().forEach(login -> {
            log.trace("检查这个用户是否需要执行删除:" + login);
            // 计划删除的时间
            LocalDateTime targetDeleteTime = login.getCreatedTime().plusMinutes(
                    timeToDelete);
            // 计划警告时间
            LocalDateTime targetWarnTime = targetDeleteTime.minusMinutes(
                    timeToWarn);
            LocalDateTime now = LocalDateTime.now();
            // 调度每10分钟进行，所以有什么事情不是10分之内可以完成的 那就先不做
            // 这个值必须跟调度它的频率 完全一致！
            LocalDateTime nextRun = now.plusMinutes(10);


            if (targetDeleteTime.isBefore(now)) {
                // 早就应该删除了
                deleteLogin(login);
            } else {
                // 是否需要调度警告？
                if (targetWarnTime.isBefore(now))
                    // 需要立刻警告
                    log.trace("需要立刻警告，但timeToWarn设置过长可能导致重复，所以这不再发送我们觉得已经超过的警告");
//                    warnDeleteLogin(login);
                else if (targetWarnTime.isBefore(nextRun)) {
                    // 下次调度前就需要了
                    long ms = now.until(targetWarnTime, ChronoUnit.MILLIS);
                    log.debug(ms + "后发布警告删除" + login);
                    scheduledExecutorService.schedule(() -> {
                        if (needDelete(login))
                            warnDeleteLogin(login);
                    }, ms, TimeUnit.MILLISECONDS);
                }

                // 是否需要调度删除
                if (targetDeleteTime.isBefore(nextRun)) {
                    long ms = now.until(targetDeleteTime, ChronoUnit.MILLIS);
                    log.debug(ms + "后删除" + login);
                    scheduledExecutorService.schedule(() -> {
                        if (needDelete(login))
                            deleteLogin(login);
                    }, ms, TimeUnit.MILLISECONDS);
                }

            }
        });
    }

    /**
     * @param login 老的登录
     * @return 是否有必要删除
     */
    private boolean needDelete(Login login) {
        Login currentLogin = loginRepository.getOne(login.getId());

        return loginRepository.countByGuideUser(currentLogin) == 0
                && !(currentLogin instanceof Manager)
                && !currentLogin.isSuccessOrder()
                && agentLevelRepository.findByLogin(currentLogin).isEmpty()
                && entityManager.find(Salesman.class, login.getId()) == null;
    }

    private void warnDeleteLogin(Login login) {
        log.debug(login + "要被警告了哦！");
        if (login.getGuideUser() != null) {
            String mobile = readService.mobileFor(login);
            userNoticeService.sendMessage(null, toWechatUser(Collections.singleton(login.getGuideUser())), null
                    , new DeleteLoginWarn()
                    , Date.from(ZonedDateTime.of(login.getCreatedTime(), ZoneId.systemDefault()).toInstant())
                    , StringUtils.isEmpty(mobile) ? "未知" : mobile);
        }
    }

    @PostConstruct
    public void init() {
        wechatNoticeHelper.registerTemplateMessage(new DeleteLoginWarn(), null);
        wechatNoticeHelper.registerTemplateMessage(new DeleteLogin(), null);
    }

    private void deleteLogin(Login login) {
        log.debug(login + "要被删除了！");
        if (login.getGuideUser() != null) {
            String mobile = readService.mobileFor(login);
            userNoticeService.sendMessage(null, toWechatUser(Collections.singleton(login.getGuideUser())), null
                    , new DeleteLogin()
                    , Date.from(ZonedDateTime.of(login.getCreatedTime(), ZoneId.systemDefault()).toInstant())
                    , StringUtils.isEmpty(mobile) ? "未知" : mobile);
        }
        loginRepository.delete(login);
    }

    @Override
    public void preDestroy() {
        scheduledExecutorService.shutdown();
    }

    private User toUser(final WeixinUserDetail detail) {
        return new User() {
            @Override
            public boolean supportNoticeChannel(NoticeChannel channel) {
                return channel == WechatNoticeChannel.templateMessage;
            }

            @Override
            public Map<String, Object> channelCredential(NoticeChannel channel) {
                Map<String, Object> map = new HashMap<>();
                map.put(WechatNoticeChannel.OpenIdCredentialTo, detail.getOpenId());
                return map;
            }
        };
    }

    /**
     * 即将删除的通知
     */
    private class DeleteLoginWarn implements MarketUserNoticeType {


        @Override
        public Collection<? extends TemplateMessageParameter> parameterStyles() {
            return Arrays.asList(
                    new SimpleTemplateMessageParameter("first", "您有一位团队成员即将被移除。")
                    , new SimpleTemplateMessageParameter("keyword1", "{0,date,yyyy-MM-dd HH:mm}")
                    , new SimpleTemplateMessageParameter("keyword2", "没有完成首笔订单")
                    , new SimpleTemplateMessageParameter("keyword3", "无")
                    , new SimpleTemplateMessageParameter("remark", "手机号码:{1};加油!")
            );
        }

        @Override
        public Class<?>[] expectedParameterTypes() {
            return new Class<?>[]{
                    Date.class,
                    // 手机号码 没有就写入未知
                    String.class
            };
        }


        @Override
        public MarketNoticeType type() {
            return MarketNoticeType.TeamMemberDeleteWarn;
        }

        @Override
        public String title() {
            return "新会员即将被删除";
        }

        @Override
        public boolean allowDifferentiation() {
            return true;
        }

        @Override
        public String defaultToText(Locale locale, Object[] parameters) {
            return null;
        }

        @Override
        public String defaultToHTML(Locale locale, Object[] parameters) {
            return null;
        }
    }

    private class DeleteLogin implements MarketUserNoticeType {

        @Override
        public Collection<? extends TemplateMessageParameter> parameterStyles() {
            return Arrays.asList(
                    new SimpleTemplateMessageParameter("first", "您有一位团队成员被移除了。")
                    , new SimpleTemplateMessageParameter("keyword1", "{0,date,yyyy-MM-dd HH:mm}")
                    , new SimpleTemplateMessageParameter("keyword2", "没有完成首笔订单")
                    , new SimpleTemplateMessageParameter("keyword3", "无")
                    , new SimpleTemplateMessageParameter("remark", "手机号码:{1}")
            );
        }

        @Override
        public Class<?>[] expectedParameterTypes() {
            return new Class<?>[]{
                    Date.class, String.class
            };
        }

        @Override
        public MarketNoticeType type() {
            return MarketNoticeType.TeamMemberDeleteNotify;
        }

        @Override
        public String title() {
            return "新会员被删除";
        }

        @Override
        public boolean allowDifferentiation() {
            return true;
        }

        @Override
        public String defaultToText(Locale locale, Object[] parameters) {
            return null;
        }

        @Override
        public String defaultToHTML(Locale locale, Object[] parameters) {
            return null;
        }

    }
}
