package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainProduct;
import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.Commission;
import cn.lmjia.market.core.entity.financing.AgentGoodAdvancePayment;
import cn.lmjia.market.core.entity.financing.AgentGoodAdvancePayment_;
import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder;
import cn.lmjia.market.core.entity.order.AgentPrepaymentOrder_;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.entity.support.TagType;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest_;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.repository.MainProductRepository;
import cn.lmjia.market.core.repository.TagRepository;
import cn.lmjia.market.core.repository.channel.ChannelRepository;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SystemService;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.lib.seext.NumberUtils;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.ProductType;
import me.jiangcai.logistics.repository.DepotRepository;
import me.jiangcai.logistics.repository.ProductRepository;
import me.jiangcai.logistics.repository.ProductTypeRepository;
import me.jiangcai.wx.standard.entity.StandardWeixinUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service("readService")
public class ReadServiceImpl implements ReadService {

    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private SystemService systemService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MainProductRepository mainProductRepository;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private TagRepository tagRepository;

    @Override
    public String nameForAgent(AgentLevel agentLevel) {
        String level;
        if (!StringUtils.isEmpty(agentLevel.getLevelTitle()))
            level = agentLevel.getLevelTitle();
        else
            level = getLoginTitle(agentLevel.getLevel());
        String rank;
        if (!StringUtils.isEmpty(agentLevel.getRank()))
            rank = agentLevel.getRank();
        else
            rank = nameForPrincipal(agentLevel.getLogin());
        return rank + "(" + level + ")";
    }

    @Override
    public String mobileFor(Object principal) {
        if (principal == null)
            return "";
        Login login = toLogin(principal);
        ContactWay contactWay = loginRepository.getOne((login).getId()).getContactWay();
        if (contactWay == null)
            return login.getLoginName();
        if (StringUtils.isEmpty(contactWay.getMobile()))
            return login.getLoginName();
        return contactWay.getMobile();
    }

    @Override
    public String nameForPrincipal(Object principal) {
        if (principal == null)
            return "";
        final Login login = toLogin(principal);
        ContactWay contactWay = loginRepository.getOne(login.getId()).getContactWay();
        if (contactWay == null)
            return login.getLoginName();
        if (StringUtils.isEmpty(contactWay.getName()))
            return login.getLoginName();
        return contactWay.getName();
    }

    @Override
    public String joinUsedNamesForPrincipal(Object principal, CharSequence delimiter) {
        if (principal == null)
            return "";
        final Login login = toLogin(principal);
        ContactWay contactWay = loginRepository.getOne(login.getId()).getContactWay();
        if (contactWay == null)
            return "";
        if (contactWay.getUsedNames() == null)
            return "";
        return contactWay.getUsedNames().stream().collect(Collectors.joining(delimiter == null ? " " : delimiter));
    }

    @Override
    public String wechatNickNameForPrincipal(Object principal) {
        final Login login = toLogin(principal);
        // 这个时候不见得有 刷新下
        StandardWeixinUser user = loginRepository.getOne(login.getId()).getWechatUser();
        if (user != null)
            return user.getNickname();
        return nameForPrincipal(principal);
    }

    @Override
    public Address addressFor(Object principal) {
        if (principal == null)
            return null;
        final Login login = toLogin(principal);
        ContactWay contactWay = loginRepository.getOne(login.getId()).getContactWay();
        if (contactWay == null)
            return null;
        return contactWay.getAddress();
    }

    private Login toLogin(Object principal) {
        final Login login;
        if (principal instanceof AgentLevel) {
            login = ((AgentLevel) principal).getLogin();
        } else
            login = (Login) principal;
        return login;
    }

    @Override
    public String percentage(BigDecimal input) {
        return NumberUtils.normalPercentage(input);
    }

    @Override
    public Money currentGoodAdvancePaymentBalance(Object principal) {
        Login login = toLogin(principal);
        login = loginRepository.getOne(login.getId());
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> sumQuery = criteriaBuilder.createQuery(BigDecimal.class);
        Root<AgentGoodAdvancePayment> root = sumQuery.from(AgentGoodAdvancePayment.class);

        sumQuery = sumQuery
                .select(criteriaBuilder.sum(root.get(AgentGoodAdvancePayment_.amount)))
                .where(
                        criteriaBuilder.equal(root.get(AgentGoodAdvancePayment_.login), login)
                        , AgentGoodAdvancePayment.isSuccessPayment(root, criteriaBuilder)
                );

        BigDecimal current;
        try {
            current = entityManager.createQuery(sumQuery).getSingleResult();
            if (current == null)
                current = BigDecimal.ZERO;
        } catch (NoResultException | NullPointerException ignored) {
            current = BigDecimal.ZERO;
        }
        // 减去非关闭的订单
        sumQuery = criteriaBuilder.createQuery(BigDecimal.class);
        Root<AgentPrepaymentOrder> orderRoot = sumQuery.from(AgentPrepaymentOrder.class);
        sumQuery = sumQuery.select(criteriaBuilder.sum(orderRoot.get(AgentPrepaymentOrder_.goodTotalPriceAmountIndependent)))
                .where(criteriaBuilder.and(
                        criteriaBuilder.equal(orderRoot.get(AgentPrepaymentOrder_.belongs), login)
                        , criteriaBuilder.notEqual(orderRoot.get(AgentPrepaymentOrder_.orderStatus), OrderStatus.close)
                ));
        try {
            current = current.subtract(entityManager.createQuery(sumQuery).getSingleResult());
        } catch (NoResultException | NullPointerException ignored) {
        }

        return new Money(current);
    }

    @Override
    public Money currentBalance(Object principal) {
        Login login = toLogin(principal);
        login = loginRepository.getOne(login.getId());
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> sumQuery = criteriaBuilder.createQuery(BigDecimal.class);
        Root<Commission> root = sumQuery.from(Commission.class);

        sumQuery = sumQuery.select(criteriaBuilder.sum(root.get("amount")))
                .where(
                        Commission.listRealitySpecification(login, null)
                                .toPredicate(root, sumQuery, criteriaBuilder)
                )
                .groupBy()
        ;

        BigDecimal current;
        try {
            current = entityManager.createQuery(sumQuery).getSingleResult().add(login.getCommissionBalance());
        } catch (NoResultException | NullPointerException ignored) {
            current = login.getCommissionBalance();
        }

        // 减去已提现或者正在提现的
        sumQuery = criteriaBuilder.createQuery(BigDecimal.class);
        Root<WithdrawRequest> withdrawRequestRoot = sumQuery.from(WithdrawRequest.class);
        sumQuery = sumQuery.select(criteriaBuilder.sum(withdrawRequestRoot.get(WithdrawRequest_.amount)))
                .where(criteriaBuilder.and(
                        criteriaBuilder.equal(withdrawRequestRoot.get(WithdrawRequest_.whose), login)
                        , withdrawRequestRoot.get(WithdrawRequest_.withdrawStatus)
                                .in(WithdrawStatus.checkPending, WithdrawStatus.success)
                ));
        try {
            current = current.subtract(entityManager.createQuery(sumQuery).getSingleResult());
        } catch (NoResultException | NullPointerException ignored) {
        }

        return new Money(current);

    }

    @Override
    public String avatarFor(Object principal) {
        Login login = toLogin(principal);
        login = loginRepository.getOne(login.getId());
        if (login.getWechatUser() != null)
            return login.getWechatUser().getHeadImageUrl();
        return systemService.toUrl("/wechat-resource/assets/img/avatar.jpg");
    }

    @Override
    public int ageForCustomer(Customer customer) {
        return LocalDate.now().getYear() - customer.getBirthYear();
    }

    @Override
    public int agentLevelForPrincipal(Object principal) {
        final Login login = toLogin(principal);
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> integerCriteriaQuery = criteriaBuilder.createQuery(Integer.class);
        Root<Login> root = integerCriteriaQuery.from(Login.class);
        integerCriteriaQuery.select(ReadService.agentLevelForLogin(root, criteriaBuilder))
                .where(criteriaBuilder.equal(root, login))
        ;
        return entityManager.createQuery(integerCriteriaQuery).getSingleResult();
    }

    @Override
    public List<Depot> allEnabledDepot() {
        return depotRepository.findByEnableTrue();
    }

    @Override
    public List<MainProduct> allEnabledMainProduct() {
        return mainProductRepository.findByEnableTrue();
    }

    @Override
    public List<Channel> allChannel() {
        return channelRepository.findAll();
    }

    @Override
    public List<Product> allEnabledProduct() {
        return productRepository.findByEnableTrue();
    }

    //    @Override
    @Override
    public String[] titles() {
        String[] titles = new String[systemService.systemLevel()];
        for (int i = 0; i < titles.length; i++) {
            titles[i] = getLoginTitle(i);
        }
        return titles;
    }

    @Override
    public List<ProductType> allProductType() {
        return productTypeRepository.findAll();
    }

    @Override
    public TagType[] allTagType() {
        return TagType.values();
    }

    @Override
    public List<Tag> allEnabledTag() {
        return tagRepository.findByDisabledFalseOrderByWeightDesc();
    }

    @Override
    public List<Tag> allTagByType(int tagType) {
        return tagRepository.findByTypeAndDisabledFalseOrderByWeightDesc(TagType.values()[tagType]);
    }

}
