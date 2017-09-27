package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.channel.Channel;
import cn.lmjia.market.core.entity.channel.InstallmentChannel;
import me.jiangcai.logistics.entity.ProductType;
import me.jiangcai.logistics.entity.PropertyName;
import me.jiangcai.logistics.repository.ProductTypeRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MainGoodServiceTest extends CoreServiceTest {

    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Test
    public void forSale() throws Exception {
        List<MainGood> originGoodList = mainGoodService.forSale();

        Channel extraChannel = new Channel();
        extraChannel.setName(RandomStringUtils.randomAlphanumeric(10));
        extraChannel.setExtra(true);

        extraChannel = channelService.saveChannel(extraChannel);
        // 随便找一个设置为该渠道的
        channelService.setupChannel(originGoodList.stream().max(new RandomComparator()).orElse(null), extraChannel);

        // 因为投融家为额外渠道 所以总量应该减少
        assertThat(mainGoodService.forSale().size())
                .isEqualTo(originGoodList.size() - 1);

        assertThat(mainGoodService.forSale(extraChannel).size())
                .isEqualTo(1);

        // 价格测试
        InstallmentChannel installmentChannel = new InstallmentChannel();
        installmentChannel.setPoundageRate(new BigDecimal(Math.abs(random.nextDouble())));
        installmentChannel.setName(RandomStringUtils.randomAlphanumeric(10));
        installmentChannel = channelService.saveChannel(installmentChannel);

        channelService.setupChannel(mainGoodService.forSale().stream().max(new RandomComparator()).orElse(null), installmentChannel);

        mainGoodService.priceCheck();
    }

    @Test
    @Ignore
    public void forSaleByPropertyValue() {
        // TODO: 2017-09-27 自己造几个多规格属性的类型，和货品
        //以量子项链为例,它有2个属性：颜色+尺寸
        ProductType productType = productTypeRepository.findTop1ByName("量子项链");
        List<MainGood> expectGoodListByType = mainGoodService.forSaleByProductType(null, productType);
        assertThat(expectGoodListByType.size()).isGreaterThanOrEqualTo(2);
        List<MainGood> searchByNoProperty = mainGoodService.forSaleByPropertyValue(null, expectGoodListByType.get(0), null);
        assertThat(searchByNoProperty.size()).isEqualTo(expectGoodListByType.size());

        MainGood detailGood = expectGoodListByType.get(0);
        //颜色属性
        PropertyName color = productType.getSpecPropertyNameList().stream().filter(p -> "颜色".equals(p.getName())).findFirst().orElse(null);
        assertThat(color).isNotNull();

        String colorValue = detailGood.getProduct().getPropertyNameValues().get(
                detailGood.getProduct().getPropertyNameValues().keySet().stream().filter(p -> p.getId().equals(color.getId())).findAny().get());

        //尺寸属性
        PropertyName size = productType.getSpecPropertyNameList().stream().filter(p -> "尺寸".equals(p.getName())).findFirst().orElse(null);
        assertThat(size).isNotNull();

        String sizeValue = detailGood.getProduct().getPropertyNameValues().get(
                detailGood.getProduct().getPropertyNameValues().keySet().stream().filter(p -> p.getId().equals(size.getId())).findAny().get());

        //先选择颜色属性，返回应该还是有2个商品
        Map<Long, String> propertyValueMap = new HashMap<>();
        propertyValueMap.put(color.getId(), colorValue);
        List<MainGood> searchByColor = mainGoodService.forSaleByPropertyValue(null, detailGood, propertyValueMap);
        assertThat(searchByColor.size()).isEqualTo(expectGoodListByType.size());

        //在选择尺寸属性，返回应该只有一个商品了
        propertyValueMap.put(size.getId(), sizeValue);
        List<MainGood> searchByColorAndSize = mainGoodService.forSaleByPropertyValue(null, detailGood, propertyValueMap);
        assertThat(searchByColorAndSize.size()).isEqualTo(1);
        assertThat(searchByColorAndSize.get(0)).isEqualTo(detailGood);

        //只选择尺寸，应该也只有一个商品
        propertyValueMap.clear();
        propertyValueMap.put(size.getId(), sizeValue);
        List<MainGood> searchBySize = mainGoodService.forSaleByPropertyValue(null, detailGood, propertyValueMap);
        assertThat(searchBySize.size()).isEqualTo(1);
        assertThat(searchBySize.get(0)).isEqualTo(detailGood);

    }

}