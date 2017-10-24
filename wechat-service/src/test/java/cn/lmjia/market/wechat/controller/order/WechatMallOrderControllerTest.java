package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.core.entity.support.TagType;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.repository.TagRepository;
import cn.lmjia.market.core.service.MainGoodService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import cn.lmjia.market.wechat.page.mall.MallCartPage;
import cn.lmjia.market.wechat.page.mall.MallGoodsDetailPage;
import cn.lmjia.market.wechat.page.mall.MallIndexPage;
import cn.lmjia.market.wechat.page.mall.MallOrderPlacePage;
import me.jiangcai.logistics.entity.ProductType;
import me.jiangcai.logistics.repository.ProductTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by helloztt on 2017-09-28.
 */
public class WechatMallOrderControllerTest extends WechatTestBase {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private MainGoodRepository mainGoodRepository;
    @Autowired
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private MainGoodService mainGoodService;

    private ProductType productType;
    List<MainGood> goodForType;

    @Before
    public void init(){
        // 在微信端发起请求
        Login login1 = randomLogin(false);
        // 特别的设计，让这个帐号绑定到开发个人微信openId 确保可以收到消息
        bindDeveloperWechat(login1);
        updateAllRunWith(login1);
        //以 立式净水机 为例，因为他有2种颜色
        productType = productTypeRepository.findTop1ByName("立式净水机");
        goodForType = mainGoodService.forSale(null,productType,null,null);
    }

    /**
     * 从商城首页随机选一个商品进入
     */
    public MallGoodsDetailPage goodsDetailFormIndex() throws IOException, InterruptedException {
        //先看看有没有商城列表的标签
        List<Tag> listTags = tagRepository.findByTypeAndDisabledFalseOrderByWeightDesc(TagType.LIST);
        if(CollectionUtils.isEmpty(listTags)){
            //如果没有就造一个
            listTags = Arrays.asList(newRandomTag(TagType.LIST));
        }
        //看看有没有商品属于这些标签
        List<MainGood> goodsForListTag = mainGoodService.forSale(null,productType,null
                ,listTags.stream().map(Tag::getName).toArray(String[]::new));
        if(CollectionUtils.isEmpty(goodsForListTag)){
            //如果没有就随便找一个添加这个标签
            MainGood randomGood = goodForType.stream().findAny().get();
            if (randomGood.getTags() == null)
                randomGood.setTags(new HashSet<>());
            randomGood.getTags().addAll(listTags);
            mainGoodRepository.save(randomGood);
            goodsForListTag = Arrays.asList(randomGood);
        }
        //数据造好了，那就开始吧
        driver.get("http://localhost/wechatIndex");
        MallIndexPage mallIndexPage = initPage(MallIndexPage.class);
        //随便找个立式净水器的商品
        MainGood testGood = goodsForListTag.stream().findAny().get();
        assertThat(testGood).isNotNull();
        mallIndexPage.clickGoods(testGood.getTags().stream().findAny().get(),testGood);
        //然后到了商品详情页
        MallGoodsDetailPage testGoodDetailPage = initPage(MallGoodsDetailPage.class);
        testGoodDetailPage.validateSameTypeGoods(testGood,goodForType);
        return testGoodDetailPage;
    }

    @Test
    public void makeOrderFromCart() throws IOException, InterruptedException {
        //先清一下购物车
        driver.get("http://localhost/wechatIndex");
        MallIndexPage mallIndexPage = initPage(MallIndexPage.class);
        mallIndexPage.clickCart();
        MallCartPage cartPage = initPage(MallCartPage.class);
        //如果购物车里有东西就清掉
        cartPage.deleteAllCart();

        //进入商品详情页
        MallGoodsDetailPage testGoodDetailPage = goodsDetailFormIndex();
        //把每个商品都加进购物车
        testGoodDetailPage.addGoodsToCard();
        cartPage = initPage(MallCartPage.class);
        //看看购物车有没有这些东西
        cartPage.validateGoods(goodForType);

        //全选，结算
        cartPage.clickCheckAll();
        cartPage.clickSettlement();

        //到填写详细信息页面
        MallOrderPlacePage orderPlacePage = initPage(MallOrderPlacePage.class);
        orderPlacePage.submitOrder();
        orderPlacePage.printThisPage();
        PaySuccessPage.waitingForSuccess(this, driver, 3, "http://localhost/wechatPaySuccess?mainOrderId=1");
        cartPage.printThisPage();


    }
}
