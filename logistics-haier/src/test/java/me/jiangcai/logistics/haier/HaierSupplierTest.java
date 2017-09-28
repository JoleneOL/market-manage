package me.jiangcai.logistics.haier;

import me.jiangcai.logistics.LogisticsService;
import me.jiangcai.logistics.LogisticsTestBase;
import me.jiangcai.logistics.entity.Depot;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.exception.UnnecessaryShipException;
import me.jiangcai.logistics.haier.entity.HaierDepot;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = HaierConfig.class)
public class HaierSupplierTest extends LogisticsTestBase {

    @Autowired
    private HaierSupplier haierSupplier;
    @Autowired
    private LogisticsService logisticsService;
    @Autowired
    private Environment environment;

    @Test
    public void product() {
        if (!haierApiTestSupport())
            return;
        haierSupplier.updateProduct(randomProductData());
    }

    private boolean haierApiTestSupport() {
        return environment.getProperty("_haier_api_test", Boolean.class, false);
    }

    @Test
    public void sign() throws UnsupportedEncodingException, DecoderException {
        if (!haierApiTestSupport())
            return;
        String content = "\uFEFF{\"sourcesn\":\"227e2ba80afa42c9b5fab7e334de8d48\",\"busflag\":\"1\",\"orderno\":\"227e2ba80afa42c9b5fab7e334de8d48\",\"city\":\"pur市\",\"county\":\"VgV区\",\"mobile\":\"17694614718\",\"orderdate\":\"2017-07-24 16:01:08\",\"bustype\":\"2\",\"storecode\":\"kV9E\",\"province\":\"YHf省\",\"name\":\"AhX人\",\"expno\":\"227e2ba80afa42c9b5fab7e334de8d48\",\"addr\":\"FTFHGg\",\"items\":[{\"number\":5,\"productcode\":\"3jjecj\",\"itemno\":1,\"prodes\":\"wMjN产品\",\"storagetype\":\"10\"},{\"number\":7,\"productcode\":\"UyYIBw\",\"itemno\":2,\"prodes\":\"wmtP产品\",\"storagetype\":\"10\"}],\"ordertype\":\"3\"}";
        String keyValue = "RRS,123";

        final String hex = DigestUtils.md5Hex(content + keyValue);
        System.out.println(content + keyValue);
        System.out.println(hex);
        System.out.println(Base64.getEncoder().encodeToString(hex.getBytes()));
        //只有生成出来的为 597221d49e3195fc1e7f2420dd678b47 才可以满足要求
//        System.out.println(Base64.getEncoder().encodeToString("597221d49e3195fc1e7f2420dd678b47".getBytes()));

        assertThat(haierSupplier.sign(content, keyValue))
                .isEqualTo("ZTQwYWU1N2MwZDgzNzc3ZTVmOTgyNzY1N2UyY2Y5ZTU=");
    }

    // 临时入库
    @Test
    public void tempIn() throws UnnecessaryShipException {
        if (!haierApiTestSupport())
            return;
        logisticsService.makeShift(haierSupplier, null, Collections.singleton(randomThing()), randomSource(), randomDepot());
//        Set<Thing> goods = new HashSet<>();
//        // uXkelZ和KWkLZc
//        goods.add(newTempThing("KWkLZc"));
//        goods.add(newTempThing("uXkelZ"));
//        Distribution distribution = haierSupplier.makeShift(randomStorage(), randomDestination(), null, LogisticsOptions.CargoToStorage);
//        System.out.println(distribution.getId());
        // 在日日顺实现中 如果是入库的话，实现是 目的，来源
        // 这个是存在极大问题的！
    }


    @Test
    public void go() throws UnnecessaryShipException {
        if (!haierApiTestSupport())
            return;
        logisticsService.makeShift(haierSupplier, null, Collections.singleton(randomThing()), randomDepot(), randomDestination());
//        Set<Thing> goods = new HashSet<>();
//        // uXkelZ和KWkLZc
//        goods.add(newTempThing("KWkLZc"));
//        goods.add(newTempThing("uXkelZ"));
//        Distribution distribution = haierSupplier.makeShift(randomStorage(), randomDestination(), null, LogisticsOptions.Installation | LogisticsOptions.CargoFromStorage);
//        haierSupplier.cancelOrder(distribution.getId(), true, null);
    }

    @Override
    protected void postNewProduct(Product product) {
        super.postNewProduct(product);
        // KWkLZc uXkelZ
        product.setCode("KWkLZc");
    }

    @Override
    protected Depot newDepot() {
        return new HaierDepot();
    }

    @Override
    protected void postNewDepot(Depot depot) {
        super.postNewDepot(depot);
        HaierDepot haierDepot = (HaierDepot) depot;
        haierDepot.setHaierCode("C12101");
    }

}