package me.jiangcai.logistics;

import lombok.Getter;
import me.jiangcai.jpa.entity.support.Address;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.logistics.demo.LogisticsEventCatch;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.entity.support.ProductStatus;
import me.jiangcai.logistics.event.InstallationEvent;
import me.jiangcai.logistics.event.ShiftEvent;
import me.jiangcai.logistics.repository.DepotRepository;
import me.jiangcai.logistics.repository.ProductRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.assertj.core.api.AbstractObjectAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 基本的物流测试
 *
 * @author CJ
 */
@ContextConfiguration(classes = {LogisticsTestBaseConfig.class})
public abstract class LogisticsTestBase extends SpringWebTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private LogisticsEventCatch logisticsEventCatch;

    /**
     * @return 断言刚收到的shiftEvent
     */
    protected AbstractObjectAssert<?, ShiftEvent> assertLastShiftEvent() {
        return assertThat(logisticsEventCatch.getShiftEvent());
    }

    /**
     * @return 断言刚收到的InstallationEvent
     */
    protected AbstractObjectAssert<?, InstallationEvent> assertLastInstallationEvent() {
        return assertThat(logisticsEventCatch.getInstallationEvent());
    }

    protected LogisticsSource randomSource() {
        return new LogisticsSource() {
            //            @Override
//            public String getProvince() {
//                return RandomStringUtils.randomAlphabetic(3) + "省";
//            }

//            @Override
//            public String getCity() {
//                return RandomStringUtils.randomAlphabetic(3) + "市";
//            }

//            @Override
//            public String getCountry() {
//                return RandomStringUtils.randomAlphabetic(3) + "区";
//            }

            @Override
            public String getProvince() {
                return "北京市";
            }

            @Override
            public String getCity() {
                return "北京市";
            }

            @Override
            public String getCountry() {
                return "昌平区";
            }

            @Override
            public String getDetailAddress() {
                return RandomStringUtils.randomAlphabetic(6);
            }

            @Override
            public String getConsigneeName() {
                return RandomStringUtils.randomAlphabetic(3) + "人";
            }

            @Override
            public String getConsigneeMobile() {
                return randomMobile();
            }
        };
    }

    protected LogisticsDestination randomDestination() {
        return new LogisticsDestination() {
//            @Override
//            public String getProvince() {
//                return RandomStringUtils.randomAlphabetic(3) + "省";
//            }

//            @Override
//            public String getCity() {
//                return RandomStringUtils.randomAlphabetic(3) + "市";
//            }

//            @Override
//            public String getCountry() {
//                return RandomStringUtils.randomAlphabetic(3) + "区";
//            }

            @Override
            public String getProvince() {
                return "北京市";
            }

            @Override
            public String getCity() {
                return "北京市";
            }

            @Override
            public String getCountry() {
                return "昌平区";
            }

            @Override
            public String getDetailAddress() {
                return RandomStringUtils.randomAlphabetic(6);
            }

            @Override
            public String getConsigneeName() {
                return RandomStringUtils.randomAlphabetic(3) + "人";
            }

            @Override
            public String getConsigneeMobile() {
                return randomMobile();
            }
        };
    }

    /**
     * 支持覆盖以获得更多实现
     *
     * @return new Depot()
     */
    protected Depot newDepot() {
        return new Depot();
    }

    /**
     * 支持覆盖以获得更多实现
     *
     * @param depot 刚整出来的depot
     */
    protected void postNewDepot(Depot depot) {
    }

    protected final Depot randomDepotData() {
        LogisticsDestination destination = randomDestination();
        Depot depot = newDepot();
        depot.setName(RandomStringUtils.randomAlphabetic(6) + "名称");
        depot.setCreateTime(LocalDateTime.now());
        depot.setEnable(true);
        depot.setChargePeopleMobile(destination.getConsigneeMobile());
        depot.setChargePeopleName(destination.getConsigneeName());
        Address address = new Address();
        address.setProvince(destination.getProvince());
        address.setPrefecture(destination.getCity());
        address.setCounty(destination.getCountry());
        address.setOtherAddress(destination.getDetailAddress());
        depot.setAddress(address);
        postNewDepot(depot);
        return depot;
    }

    protected Depot randomNewDepot() {
        return depotRepository.save(randomDepotData());
    }

    protected Depot randomDepot() {
        return depotRepository.findAll().stream()
                .max(new RandomComparator()).orElseGet(this::randomNewDepot);
    }

    /**
     * 支持覆盖以获得更多实现
     *
     * @return new Product()
     */
    protected Product newProduct() {
        return new Product();
    }

    /**
     * 支持覆盖以获得更多实现
     *
     * @param product 刚整出来的product
     */
    protected void postNewProduct(Product product) {
    }

    /**
     * @return 随机的货品数据
     */
    protected final Product randomProductData() {
        Product product = newProduct();
        product.setEnable(true);
        product.setCode(RandomStringUtils.randomAlphabetic(6));
        product.setName(RandomStringUtils.randomAlphabetic(3) + "名称");
        product.setBrand(RandomStringUtils.randomAlphabetic(3) + "品牌");
        product.setMainCategory(RandomStringUtils.randomAlphabetic(3) + "类目");
        product.setDescription(RandomStringUtils.randomAlphabetic(10));
        product.setSKU(RandomStringUtils.randomAlphabetic(69));
        product.setUnit(RandomStringUtils.randomAlphabetic(1));
        product.setVolumeHeight(new BigDecimal(random.nextInt(100) + 10));
        product.setVolumeLength(new BigDecimal(random.nextInt(100) + 10));
        product.setVolumeWidth(new BigDecimal(random.nextInt(100) + 10));
        product.setWeight(new BigDecimal(random.nextInt(3000) + 500));
        postNewProduct(product);
        return product;
    }

    protected Product randomNewProduct() {
        return productRepository.save(randomProductData());
    }


    protected Product randomProduct() {
        return productRepository.findAll().stream()
                .max(new RandomComparator()).orElseGet(this::randomNewProduct);
    }

    protected Thing randomThing() {
        return new SimpleThing(randomProduct()
                , random.nextInt(10) + 1,
                ProductStatus.normal);
    }

    @Getter
    private class SimpleThing implements Thing {
        private final Product product;
        private final int amount;
        private final ProductStatus productStatus;

        SimpleThing(Product product, int amount, ProductStatus productStatus) {
            this.product = product;
            this.amount = amount;
            this.productStatus = productStatus;
        }
    }
}
