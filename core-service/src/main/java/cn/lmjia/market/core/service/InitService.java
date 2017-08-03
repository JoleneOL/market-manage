package cn.lmjia.market.core.service;

import cn.lmjia.market.core.Version;
import cn.lmjia.market.core.config.CoreConfig;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.MainProduct;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.channel.InstallmentChannel;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.repository.MainProductRepository;
import cn.lmjia.market.core.trj.TRJService;
import me.jiangcai.lib.jdbc.ConnectionProvider;
import me.jiangcai.lib.jdbc.JdbcService;
import me.jiangcai.lib.upgrade.VersionUpgrade;
import me.jiangcai.lib.upgrade.service.UpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Properties;

/**
 * 初始化服务
 *
 * @author CJ
 */
@Service
public class InitService {

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

    @PostConstruct
    @Transactional
    public void init() throws IOException, SQLException {
        commons();
        database();
        upgrade();
        managers();
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
        if (environment.acceptsProfiles(CoreConfig.ProfileUnitTest) || environment.acceptsProfiles("staging")) {
            if (mainGoodService.forSale(channel).isEmpty()) {
                MainGood good = mainGoodRepository.findAll((root, query, cb)
                        -> cb.equal(MainGood.getTotalPrice(root, cb), new BigDecimal("3000"))).get(0);
                channelService.setupChannel(good, channel);
            }
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
        jdbcService.runJdbcWork(JpaFunctionUtils::Enhance);
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
                    mainProduct.setCode(type);
                    mainProduct.setName(productName);
                    mainProduct.setDeposit(new BigDecimal(value[1]));
                    mainProduct.setServiceCharge(new BigDecimal(value[2]));
                    mainProduct.setInstall(new BigDecimal(value[3]));
                    mainProduct = mainProductRepository.save(mainProduct);
                }

                MainGood mainGood = mainGoodRepository.findByProduct(mainProduct);
                if (mainGood == null) {
                    mainGood = new MainGood();
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
