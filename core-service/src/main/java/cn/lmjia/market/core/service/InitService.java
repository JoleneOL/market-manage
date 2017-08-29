package cn.lmjia.market.core.service;

import cn.lmjia.market.core.Version;
import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Customer_;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.entity.MainProduct;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.channel.InstallmentChannel;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentLevel_;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import cn.lmjia.market.core.entity.request.PromotionRequest_;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.repository.MainProductRepository;
import cn.lmjia.market.core.trj.TRJService;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.lib.jdbc.ConnectionProvider;
import me.jiangcai.lib.jdbc.JdbcService;
import me.jiangcai.lib.upgrade.VersionUpgrade;
import me.jiangcai.lib.upgrade.service.UpgradeService;
import me.jiangcai.logistics.StockService;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.haier.entity.HaierDepot;
import me.jiangcai.logistics.repository.DepotRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * 初始化服务
 *
 * @author CJ
 */
@Service
public class InitService {

    private static final Log log = LogFactory.getLog(InitService.class);
    @Autowired
    private LoginService loginService;
    @Autowired
    private UpgradeService upgradeService;
    @Autowired
    private MainGoodRepository mainGoodRepository;
    @Autowired
    private MainProductRepository mainProductRepository;
    @Autowired
    private JdbcService jdbcService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private Environment environment;
    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private StockService stockService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @PostConstruct
    @Transactional
    public void init() throws IOException, SQLException {
        commons();
        database();
        upgrade();
        managers();
        depots();
        products();
        others();
    }

    private void others() {
        Channel channel = channelService.findByName(TRJService.ChannelName);
        if (channel == null) {
            // 新增投融家渠道
            final InstallmentChannel installmentChannel = new InstallmentChannel();
            installmentChannel.setPoundageRate(new BigDecimal("0.2"));
            installmentChannel.setName(TRJService.ChannelName);
            installmentChannel.setExtra(true);
            installmentChannel.setLockedAmountPerOrder(1);

            channel = channelService.saveChannel(installmentChannel);
        }

        // 在测试 或者 staging 找一款价格为3000的商品 作为投融家商品
        if ((environment.acceptsProfiles(CoreConfig.ProfileUnitTest) || environment.acceptsProfiles("staging"))
                && mainGoodService.forSale(channel).isEmpty()) {
            MainGood good = mainGoodRepository.findAll((root, query, cb)
                    -> cb.equal(MainGood.getTotalPrice(root, cb), new BigDecimal("3000"))).get(0);
            channelService.setupChannel(good, channel);
        }
    }

    private void database() throws SQLException {
        jdbcService.runJdbcWork(connection -> {
            if (connection.profile().isH2()) {
                executeSQLCode(connection, "LoginAgentLevel.h2.sql");
            } else if (connection.profile().isMySQL()) {
                try (Statement statement = connection.getConnection().createStatement()) {
                    statement.executeUpdate("DROP FUNCTION IF EXISTS `LoginAgentLevel`");
                }
                executeSQLCode(connection, "LoginAgentLevel.mysql.sql");
            }
        });
    }

    private void executeSQLCode(ConnectionProvider connection, String resourceName) throws SQLException {
        try {
            String code = StreamUtils.copyToString(applicationContext.getResource("classpath:/" + resourceName).getInputStream(), Charset.forName("UTF-8"));
            try (Statement statement = connection.getConnection().createStatement()) {
                statement.executeUpdate(code);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void commons() throws SQLException {
        jdbcService.runJdbcWork(JpaFunctionUtils::enhance);
    }

    private void depots() {
        if ((environment.acceptsProfiles(CoreConfig.ProfileUnitTest)
                || environment.acceptsProfiles("staging")) && depotRepository.count() == 0) {
            Depot depot = new HaierDepot();
            depot.setEnable(true);
            depot.setCreateTime(LocalDateTime.now());
            depot.setName("测试仓库");
            Address address = new Address();
            address.setCounty("中国");
            address.setProvince("浙江省");
            address.setPrefecture("杭州市");
            address.setOtherAddress("滨江区巴拉巴拉");
            depot.setAddress(address);
            depot.setChargePeopleName("张三");
            depot.setChargePeopleMobile("110");
            depotRepository.save(depot);
        }
    }

    private void products() throws IOException {
        if (mainProductRepository.count() > 0)
            return;
        Properties properties = new Properties();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("/defaultProducts.properties").getInputStream(), "UTF-8"))) {
            properties.load(reader);
            properties.stringPropertyNames().forEach(type -> {
                // 货品确认
                final String value[] = properties.getProperty(type).split(",");
                final String productName = value[0];

                MainProduct mainProduct = mainProductRepository.findOne(type);
                if (mainProduct == null) {
                    mainProduct = new MainProduct();
                    mainProduct.setEnable(true);
                    mainProduct.setCode(type);
                    mainProduct.setName(productName);
                    mainProduct.setDeposit(new BigDecimal(value[1]));
                    mainProduct.setServiceCharge(new BigDecimal(value[2]));
                    mainProduct.setInstall(new BigDecimal(value[3]));
                    mainProduct = mainProductRepository.save(mainProduct);
                }
                if (stockService.usableStockTotal(mainProduct) == 0) {
                    Depot depot = depotRepository.findAll().get(0);
                    stockService.addStock(depot, mainProduct, 100, "测试");
                }

                MainGood mainGood = mainGoodRepository.findByProduct(mainProduct);
                if (mainGood == null) {
                    mainGood = new MainGood();
                    mainGood.setCreateTime(LocalDateTime.now());
                    mainGood.setProduct(mainProduct);
                    mainGood.setEnable(true);
                    mainGoodRepository.save(mainGood);
                }
            });
        }

    }

    private void upgrade() {
        //noinspection Convert2Lambda
        upgradeService.systemUpgrade(new VersionUpgrade<Version>() {
            @Override
            public void upgradeToVersion(Version version) throws Exception {
                switch (version) {
                    case init:
                        break;
                    case muPartOrder:
                        try {
                            jdbcService.tableAlterAddColumn(MainProduct.class, "planSellOutDate", null);
                        } catch (Throwable ignored) {
                        }
                        jdbcService.tableAlterAddColumn(MainOrder.class, "goodTotalPriceAmountIndependent", null);
                        jdbcService.tableAlterAddColumn(MainOrder.class, "goodCommissioningPriceAmountIndependent", null);
                        jdbcService.tableAlterAddColumn(MainOrder.class, "orderBody", null);
                        jdbcService.runJdbcWork(connection -> {
                            if (connection.profile().isMySQL()) {
                                try (Statement statement = connection.getConnection().createStatement()) {
                                    try {
                                        String origin = StreamUtils.copyToString(
                                                new ClassPathResource("/upgrade/muPartOrder.sql").getInputStream()
                                                , Charset.forName("UTF-8"));
                                        StringTokenizer tokenizer = new StringTokenizer(origin, ";");
                                        while (tokenizer.hasMoreElements()) {
                                            statement.addBatch(tokenizer.nextToken());
                                        }
                                        statement.executeBatch();
                                    } catch (IOException e) {
                                        throw new IllegalStateException("what??", e);
                                    }
                                }
                            }
                        });
                        break;
                    case newLogin:
                        jdbcService.tableAlterAddColumn(Login.class, "successOrder", "0");
                        // 任意一个 拥有过 一个成功客户的订单 的Login就设置为true
                        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                        CriteriaQuery<Login> loginCq = cb.createQuery(Login.class);
                        Root<MainOrder> orderRoot = loginCq.from(MainOrder.class);
                        entityManager.createQuery(
                                loginCq.select(orderRoot.get(MainOrder_.orderBy))
                                        .where(cb.isTrue(
                                                orderRoot.get(MainOrder_.customer)
                                                        .get(Customer_.successOrder)
                                        ))
                                        .distinct(true)
                        ).getResultList().forEach(login -> login.setSuccessOrder(true));
                        //  customer 既然该表相关的量已不再重要……
                        // 创建于 customer 的Login 而且它自己未曾下过单
                        loginCq = cb.createQuery(Login.class);
                        Root<Customer> customerRoot = loginCq.from(Customer.class);
                        List<Login> all = entityManager.createQuery(loginCq
                                .select(customerRoot.get(Customer_.login))
                                .distinct(true)
                        ).getResultList();
                        // 将下过单的用户排除
                        loginCq = cb.createQuery(Login.class);
                        orderRoot = loginCq.from(MainOrder.class);
                        List<Login> reallyOrder = entityManager.createQuery(
                                loginCq.select(orderRoot.get(MainOrder_.orderBy))
                                        .distinct(true)
                        ).getResultList();
                        // 将申请过什么什么
                        loginCq = cb.createQuery(Login.class);
                        Root<PromotionRequest> requestRoot = loginCq.from(PromotionRequest.class);
                        List<Login> requested = entityManager.createQuery(
                                loginCq.select(requestRoot.get(PromotionRequest_.whose))
                                        .distinct(true)
                        ).getResultList();
                        // 将跟代理商存在关系的
                        loginCq = cb.createQuery(Login.class);
                        Root<AgentLevel> levelRoot = loginCq.from(AgentLevel.class);
                        List<Login> level = entityManager.createQuery(
                                loginCq.select(levelRoot.get(AgentLevel_.login))
                                        .distinct(true)
                        ).getResultList();
                        // 将剩下进行一次数据转换！
                        all.removeAll(reallyOrder);
                        all.removeAll(requested);
                        all.removeAll(level);
                        log.info("即将转换" + all.size() + "个身份");
                        all.forEach(login -> {
                            login.setEnabled(false);
                            login.setLoginName("!" + login.getLoginName() + " ");
                        });
                        // 对login缓存的关系呢？ 这个不管了
                        break;
                    default:
                }

            }
        });
    }

    private void managers() {
        if (loginService.managers().isEmpty()) {
            // 添加一个主管理员
            Manager manager = new Manager();
            manager.setLevelSet(Collections.singleton(ManageLevel.root));
            manager.setLoginName("root");
            loginService.password(manager, null, "654321");
        }
    }
}
