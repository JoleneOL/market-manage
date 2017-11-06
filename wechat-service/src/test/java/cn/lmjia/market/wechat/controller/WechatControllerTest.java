package cn.lmjia.market.wechat.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import cn.lmjia.market.core.entity.Tag;
import cn.lmjia.market.core.entity.support.TagType;
import cn.lmjia.market.core.repository.MainGoodRepository;
import cn.lmjia.market.core.repository.TagRepository;
import cn.lmjia.market.core.service.MainGoodService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.LoginPage;
import cn.lmjia.market.wechat.page.WechatRegisterPage;
import cn.lmjia.market.wechat.page.mall.*;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ActiveProfiles("mysql2")
public class WechatControllerTest extends WechatTestBase {
    @Autowired
    private MainGoodService mainGoodService;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private MainGoodRepository mainGoodRepository;

    @Test
    public void bindWithPassword() throws Exception {
        WeixinUserDetail detail = nextCurrentWechatAccount();

        // 使用一个陌生的微信用户 打开 /toLoginWechat 会跳转到 登录界面
        // 完成之后 则立刻跳转到主页
        // 下次再使用该帐号登录则直接来到主页

        LoginPage loginPage = getLoginPageForBrowseIndex();

        // 弄一个登录过来
        String rawPassword = UUID.randomUUID().toString();
        Login login = newRandomAgent(rawPassword);

        loginPage.login(login.getLoginName(), rawPassword + 1);
        loginPage.assertHaveTooltip();

        // 尝试使用正确的密码登录吧
        loginPage.login(login.getLoginName(), rawPassword);

        // 如何去公众号？
        initPage(MallIndexPage.class);
        assertThat(loginService.asWechat(detail.getOpenId()))
                .isNotNull();


    }

    private LoginPage getLoginPageForBrowseIndex() {
        driver.get("http://localhost/wechatIndex");
        return initPage(LoginPage.class);
    }

    @Test
    public void bindWithVCCode() throws Exception {
        WeixinUserDetail detail = nextCurrentWechatAccount();

        // 使用一个陌生的微信用户 打开 /toLoginWechat 会跳转到 登录界面
        // 完成之后 则立刻跳转到主页
        // 下次再使用该帐号登录则直接来到主页

        LoginPage loginPage = getLoginPageForBrowseIndex();

        // 弄一个登录过来
        String rawPassword = UUID.randomUUID().toString();
        Login login = newRandomAgent(rawPassword);

        loginPage.sendAuthCode(login.getLoginName());
        // 这个验证码是错误的！
        loginPage.loginWithAuthCode("9999");
        loginPage.assertHaveTooltip();

        loginPage.sendAuthCode(login.getLoginName());
        //  不能再发啦
        loginPage.assertHaveTooltip();

        // 重新打开页面
        loginPage = getLoginPageForBrowseIndex();

        // 弄一个新用户
        login = newRandomAgent(rawPassword);
        loginPage.sendAuthCode(login.getLoginName());
        loginPage.loginWithAuthCode("1234");
//        loginPage.assertHaveTooltip();
        // 如何去公众号？
        initPage(MallIndexPage.class);
        assertThat(loginService.asWechat(detail.getOpenId()))
                .isNotNull();
    }

    @Test
    public void testMallIndex() throws IOException, InterruptedException {
        LoginPage loginPage = getLoginPageForBrowseIndex();

        // 弄一个登录过来
        String rawPassword = UUID.randomUUID().toString();
        Login login = newRandomAgent(rawPassword);


        //每种标签都加3-5个
        for (TagType tagType : TagType.values()) {
            for (int i = 0; i < 3 + random.nextInt(2); i++) {
                newRandomTag(tagType);
            }
        }
        //设置一件商品有所有的 列表 类型的标签
        MainGood good = mainGoodService.forSale().stream().max(Comparator.comparing(MainGood::getId)).orElse(null);
        if (good == null)
            return;
        Set<Tag> tagTypeSet = new HashSet<>();
        tagTypeSet.addAll(tagRepository.findByTypeAndDisabledFalseOrderByWeightDesc(TagType.LIST));
        tagTypeSet.addAll(tagRepository.findByTypeAndDisabledFalseOrderByWeightDesc(TagType.SEARCH));
        good.setTags(tagTypeSet);
        mainGoodRepository.save(good);
        // 尝试使用正确的密码登录吧
        loginPage.login(login.getLoginName(), rawPassword);


        MallIndexPage indexPage = initPage(MallIndexPage.class);

        //校验滚图标签
        indexPage.validatePageWithImgTag(tagRepository.findByTypeAndDisabledFalseOrderByWeightDesc(TagType.IMG));
        //校验分类
        indexPage.validatePageWithSearch(tagRepository.findByTypeAndDisabledFalseOrderByWeightDesc(TagType.SEARCH));
        //校验列表
        indexPage.validatePageWithList(tagRepository.findByTypeAndDisabledFalseOrderByWeightDesc(TagType.LIST), good);

        //到搜索页面去
        indexPage.clickSearch();
        //搜索一下这个商品
        MallSearchPage mallSearchPage = initPage(MallSearchPage.class);
        mallSearchPage.searchGoods(good);

        //重新回到首页
        driver.get("http://localhost/wechatIndex");
        indexPage = initPage(MallIndexPage.class);
        //随便点一个分类标签
        Tag searchTag = tagRepository.findByTypeAndDisabledFalseOrderByWeightDesc(TagType.SEARCH).stream()
                .max(Comparator.comparing(Tag::getName)).orElse(null);
        assertThat(searchTag).isNotNull();
        indexPage.clickTagSearch(searchTag);

        MallTagDetailPage tagDetailPage = initPage(MallTagDetailPage.class);
        //校验一下搜索结果中是有这个商品的
        tagDetailPage.validateGoods(Arrays.asList(good));
        //查询 tag & propertyValue
        String propertyValue = good.getProduct().getSpecPropertyNameValues().values().stream().findAny().get();
        tagDetailPage.clickTagOrPropertyValue(propertyValue);
        tagDetailPage.validateGoods(Arrays.asList(good));

        //搜索全部
        tagDetailPage.clickTagOrPropertyValue(null);
        tagDetailPage.validateGoods(mainGoodService.forSale());


    }

    @Test
    public void wechatForwardTest(){
        //首先要有一个转发者
        String rawPassword = UUID.randomUUID().toString();
        Login login = newRandomAgent(rawPassword);

        //随便拿一个商品
        MainGood mainGood = mainGoodService.forSale().stream().findAny().get();
        //转发
        String shareUrl = "http://localhost/wechatForward/" + mainGood.getId() + "_" + login.getId() + "?from=singlemessage&isappinstalled=0";


        //1.自己看看
        LoginPage loginPage = getLoginPageForBrowseIndex();
        loginPage.login(login.getLoginName(), rawPassword);
        driver.get(shareUrl);
        MallGoodsDetailPage testGoodDetailPage = initPage(MallGoodsDetailPage.class);
        testGoodDetailPage.validateShareUrl(mainGood.getId(),login.getId());

        //退出登录
        driver.get("http://localhost/logout");

        //2.在没有Login的情况下访问
        driver.get(shareUrl);
        testGoodDetailPage = initPage(MallGoodsDetailPage.class);
        testGoodDetailPage.validateShareUrl(mainGood.getId(),null);

        //点立即购买，应该是跳转到注册页面
        testGoodDetailPage.clickBuyNow();
        testGoodDetailPage.printThisPage();
        WechatRegisterPage registerPage = initPage(WechatRegisterPage.class);

        //还能访问商城

        // 完成注册
        String mobile = randomMobile();
        registerPage.sendAuthCode(mobile);
        registerPage.submitSuccessAs(RandomStringUtils.randomAlphabetic(10));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {

        }

        //应该到商城首页
        MallIndexPage indexPage = initPage(MallIndexPage.class);

        //再次来到商品详情页
        driver.get(shareUrl);
        testGoodDetailPage = initPage(MallGoodsDetailPage.class);
        testGoodDetailPage.clickBuyNow();
        //到填写详细信息页面
        MallOrderPlacePage orderPlacePage = initPage(MallOrderPlacePage.class);


    }

}