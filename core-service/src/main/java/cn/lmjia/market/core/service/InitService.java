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
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.ProductType;
import me.jiangcai.logistics.entity.PropertyName;
import me.jiangcai.logistics.entity.PropertyValue;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.entity.support.PropertyType;
import me.jiangcai.logistics.haier.entity.HaierDepot;
import me.jiangcai.logistics.repository.DepotRepository;
import me.jiangcai.logistics.repository.ProductTypeRepository;
import me.jiangcai.logistics.repository.PropertyNameRepository;
import me.jiangcai.logistics.repository.PropertyValueRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @Autowired
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private PropertyNameRepository propertyNameRepository;
    @Autowired
    private PropertyValueRepository propertyValueRepository;
    @Autowired
    private MainOrderService mainOrderService;

    @PostConstruct
    @Transactional
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void init() throws IOException, SQLException {
        commons();
        database();
        upgrade();
        managers();
        productTypes();
        depots();
        products();
        others();
        mainOrderService.createExecutorToForPayOrder();
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
                    -> cb.equal(MainGood.getTotalPrice(root, cb), new BigDecimal("3000"))).stream().findFirst().orElse(null);
            if(good != null)
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
            //
            String fileName;
            if (connection.profile().isMySQL()) {
                fileName = "mysql";
            } else if (connection.profile().isH2()) {
                fileName = "h2";
            } else
                throw new IllegalStateException("not support for:" + connection.getConnection());
            try {
                try (Statement statement = connection.getConnection().createStatement()) {
                    statement.executeUpdate("DROP TABLE IF EXISTS `LoginCommissionJournal`");
                    statement.executeUpdate(StreamUtils.copyToString(new ClassPathResource(
                                    "/LoginCommissionJournal." + fileName + ".sql").getInputStream()
                            , Charset.forName("UTF-8")));
                }
            } catch (IOException e) {
                throw new IllegalStateException("读取SQL失败", e);
            }
            //

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

    private void productTypes() throws IOException {
        if (productTypeRepository.count() > 0) {
            return;
        }
        //先上属性及属性值
        Properties properties = new Properties();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("/defaultPropertyNameValues.properties").getInputStream(), "UTF-8"))) {
            properties.load(reader);
            properties.stringPropertyNames().forEach(property -> {
                //属性
                final String values[] = properties.getProperty(property).split(",");
                PropertyName propertyName = new PropertyName();
                propertyName.setName(property);
                propertyName.setType(PropertyType.valueOf(PropertyType.class, values[0]));
                propertyName.setSpec(Boolean.parseBoolean(values[1]));
                //属性值
                final String propertyValueNames[] = values[2].split("\\|");
                List<PropertyValue> propertyValueList = new ArrayList<>(propertyValueNames.length);
                for (int i = 0; i < propertyValueNames.length; i++) {
                    PropertyValue propertyValue = new PropertyValue();
                    propertyValue.setValue(propertyValueNames[i]);
                    propertyValue.setPropertyName(propertyName);
                    propertyValueList.add(propertyValue);
                }
                propertyName.setPropertyValueList(propertyValueList);
                propertyNameRepository.saveAndFlush(propertyName);
            });
        }
        properties.clear();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("/defaultProductTypes.properties").getInputStream(), "UTF-8"))) {
            properties.load(reader);
            properties.stringPropertyNames().forEach(type -> {
                final String values[] = properties.getProperty(type).split(",");
                ProductType productType = new ProductType();
                productType.setName(type);
                productType.setPath("|0|");
                List<PropertyValue> propertyValueList = new ArrayList<>();
                for (String value : values) {
                    if (!value.contains(":")) {
                        continue;
                    }
                    String name = value.split(":")[0];
                    String[] propertyValues = value.split(":")[1].split("\\|");
                    PropertyName propertyName = propertyNameRepository.findTop1ByName(name);
                    if (propertyName != null) {
                        for (int i = 0; i < propertyValues.length; i++) {
                            PropertyValue propertyValue = propertyValueRepository.findTop1ByPropertyNameAndValue(propertyName, propertyValues[i]);
                            if (propertyValue != null)
                                propertyValueList.add(propertyValue);
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(propertyValueList)) {
                    productType.setPropertyValueList(propertyValueList);
                }
                productTypeRepository.save(productType);
            });
        }
    }

    private void products() throws IOException {
        if (mainProductRepository.count() > 0) {
            // 确保当前系统中 是不会存在 productType 为null的货品
            List<ProductType> productTypeList = productTypeRepository.findAll();
            mainProductRepository.findByProductTypeNull().forEach(product -> {

                ProductType targetType = productTypeList.stream().filter(productType -> product.getName().contains(productType.getName()))
                        // 如果多个 暂时不管了…………
                        .findFirst().orElseThrow(() -> new IllegalStateException(product + "需要设置ProductType 但是却没找到合适的，升级失败。"));
                product.setProductType(targetType);
                // 属性还无法设置，建议手动设置
                // 如果可以选择就帮忙选择出来 如果无从选择 就log一下得了
                boolean propertyValueAware = false;
                for (PropertyName propertyName : targetType.getPropertyNameList()) {
                    propertyValueAware = false;
                    if ("包装规格".equalsIgnoreCase(propertyName.getName())) {
                        // 一个 和另外一个
                        PropertyValue targetPropertyValue = targetType.getPropertyValueList().stream()
                                .filter(pv -> {
                                    Integer n1 = toNumber(pv.getValue());
                                    Integer n2 = toNumber(product.getName());
                                    return (n1 == null && n2 == null) || (n1 != null && n1.equals(n2));
                                })
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException(product + "找不到准确的包装规格属性"));
                        product.setPropertyNameValues(Collections.singletonMap(propertyName, targetPropertyValue.getValue()));
                        propertyValueAware = true;
                    } else if ("颜色".equalsIgnoreCase(propertyName.getName())) {
                        PropertyValue targetPropertyValue;
                        if (targetType.getPropertyValueList().size() == 1)
                            targetPropertyValue = targetType.getPropertyValueList().get(0);
                        else {
                            targetPropertyValue = targetType.getPropertyValueList().stream()
                                    .filter(pv -> product.getName().contains(pv.getValue()))
                                    .findFirst()
                                    .orElse(
                                            // 实在找不到 则进入宽容模式
                                            targetType.getPropertyValueList().stream()
                                                    .filter(pv -> product.getName().replaceAll("黑", "").contains(pv.getValue().length() >= 3 ? pv.getValue().replaceAll("黑", "") : pv.getValue()))
                                                    .findFirst()
                                                    .orElse(null)
                                    );
                        }
                        if (targetPropertyValue != null) {
                            propertyValueAware = true;
                            product.setPropertyNameValues(Collections.singletonMap(propertyName, targetPropertyValue.getValue()));
                        }
                    }
                }

                if (!propertyValueAware && product.isEnable())
                    log.info(product + "需要设置货品属性");

                mainProductRepository.save(product);
            });
            return;
        }
        Properties properties = new Properties();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("/defaultProducts.properties").getInputStream(), "UTF-8"))) {
            properties.load(reader);
            properties.stringPropertyNames().forEach(type -> {
                // 货品确认
                final String value[] = properties.getProperty(type).split(",");
                final String productName = value[0];

                MainProduct mainProduct = mainProductRepository.findOne(type);
                ProductType productType = productTypeRepository.findTop1ByName(productName);
                if (mainProduct == null) {
                    mainProduct = new MainProduct();
                    mainProduct.setEnable(true);
                    mainProduct.setCode(type);
                    mainProduct.setName(productName);
                    mainProduct.setDeposit(new BigDecimal(value[1]));
                    mainProduct.setServiceCharge(new BigDecimal(value[2]));
                    mainProduct.setInstall(new BigDecimal(value[3]));
                    if (productType != null) {
                        mainProduct.setProductType(productType);
                        if (value.length > 4) {
                            String[] propertyValues = value[4].split("\\|");
                            Map<PropertyName, String> propertyNameValue = new HashMap<>();
                            for (String propertyNameValueStr : propertyValues) {
                                PropertyName propertyName = productType.getPropertyNameList().stream()
                                        .filter(p -> p.getName().equals(propertyNameValueStr.split(":")[0])).findFirst().orElse(null);
                                if (propertyName != null) {
                                    propertyNameValue.put(propertyName, propertyNameValueStr.split(":")[1]);
                                }
                            }
                            mainProduct.setPropertyNameValues(propertyNameValue);
                        }
                    }
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

    private Integer toNumber(String value) {
        Matcher matcher = Pattern.compile(".*(\\d+).*").matcher(value);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
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
                    case muPartShift:
                        jdbcService.tableAlterAddColumn(MainOrder.class, "ableShip", "1");
                        jdbcService.tableAlterAddColumn(StockShiftUnit.class, "installation", "0");
                        break;
                    case salesman:
                        break;
                    case mall:
                        jdbcService.tableAlterAddColumn(Product.class, "mainImg", null);
                        jdbcService.runJdbcWork(connection -> {
                            try (Statement statement = connection.getConnection().createStatement()) {
                                statement.execute("ALTER TABLE PRODUCT ADD COLUMN PRODUCTTYPE_ID BIGINT;");
                                statement.execute("ALTER TABLE PRODUCT ADD CONSTRAINT FK_PRODUCT_PRODUCTTYPE_ID FOREIGN KEY (PRODUCTTYPE_ID) REFERENCES PRODUCTTYPE (ID)");
                                statement.execute("UPDATE `PRODUCT` SET `NAME`='台式净水机' WHERE `NAME`='台式净水器'");
                            }
                        });
                        break;
                    default:
                }

            }
        });
    }

    private void managers() {
        if (loginService.managers().isEmpty()) {
            // 添加一个默认管理员
            Manager manager = new Manager();
            manager.setLevelSet(Collections.singleton(ManageLevel.root));
            manager.setLoginName("root");
            loginService.password(manager, null, "654321");
        }
        if (environment.acceptsProfiles("staging")) {
            // 在staging 环境中 root 密码总是稳定的
            loginService.password(loginService.byLoginName("root"), "654321");
        }
    }
}
