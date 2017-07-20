package me.jiangcai.logistics.haier;

import me.jiangcai.logistics.Destination;
import me.jiangcai.logistics.LogsticsTest;
import me.jiangcai.logistics.Storage;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.option.LogisticsOptions;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@ContextConfiguration(classes = HaierConfig.class)
public class HaierSupplierTest extends LogsticsTest {

    @Autowired
    private HaierSupplier haierSupplier;

    @Test
    public void go() {
        haierSupplier.makeDistributionOrder(randomStorage(), randomThings(), randomDestination(), LogisticsOptions.Installation | LogisticsOptions.CargoFromStorage);
    }

    private Destination randomDestination() {
        return new Destination() {
            @Override
            public String getProvince() {
                return RandomStringUtils.randomAlphabetic(3) + "省";
            }

            @Override
            public String getCity() {
                return RandomStringUtils.randomAlphabetic(3) + "市";
            }

            @Override
            public String getCountry() {
                return RandomStringUtils.randomAlphabetic(3) + "区";
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

    private Collection<Thing> randomThings() {
        final Stream.Builder<Thing> builder = Stream
                .builder();
        int x = random.nextInt(2) + 1;
        while (x-- > 0)
            builder.add(randomThing());
        return builder.build()
                .collect(Collectors.toSet());
    }

    private Thing randomThing() {
        return new Thing() {
            @Override
            public String getProductCode() {
                return RandomStringUtils.randomAlphanumeric(6);
            }

            @Override
            public String getProductName() {
                return RandomStringUtils.randomAlphabetic(4) + "产品";
            }

            @Override
            public int getAmount() {
                return random.nextInt(10) + 1;
            }
        };
    }

    private Storage randomStorage() {
        return new Storage() {
            @Override
            public String getStorageCode() {
                return RandomStringUtils.randomAlphanumeric(4);
            }
        };
    }

}