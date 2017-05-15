package cn.lmjia.market.core.service;

import cn.lmjia.market.core.Version;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.Product;
import cn.lmjia.market.core.entity.ProductType;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.core.repository.ProductRepository;
import cn.lmjia.market.core.repository.ProductTypeRepository;
import me.jiangcai.lib.upgrade.VersionUpgrade;
import me.jiangcai.lib.upgrade.service.UpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
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
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private ProductRepository productRepository;

    @PostConstruct
    @Transactional
    public void init() throws IOException {
        upgrade();
        managers();
        products();
    }

    private void products() throws IOException {
        Properties properties = new Properties();
        try (final InputStream inputStream = new ClassPathResource("/defaultProducts.properties").getInputStream()) {
            properties.load(inputStream);
            properties.stringPropertyNames().forEach(type -> {
                ProductType productType = productTypeRepository.findOne(type);
                if (productType == null) {
                    final String productName = properties.getProperty(type);
                    Product product = productRepository.findByName(productName);
                    if (product == null) {
                        product = new Product();
                        product.setName(productName);
                        product = productRepository.save(product);
                    }
                    productType = new ProductType();
                    productType.setProduct(product);
                    productType.setId(type);
                    productTypeRepository.save(productType);
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
            manager.setLevel(ManageLevel.root);
            manager.setLoginName("root");
            loginService.password(manager, "rootIsRoot");
        }
    }
}
