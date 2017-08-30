package cn.lmjia.market.core.service.request;

import cn.lmjia.market.core.define.MarketUserNoticeType;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import cn.lmjia.market.core.service.WechatNoticeHelper;
import me.jiangcai.jpa.entity.support.Address;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 升级申请服务
 * 支付通知：OPENTM409997458
 * 通过通知：OPENTM205211943
 *
 * @author CJ
 */
public interface PromotionRequestService {

    /**
     * @param login
     * @return 当前申请；或者null
     */
    @Transactional(readOnly = true)
    PromotionRequest currentRequest(Login login);

    /**
     * 初始化一个申请，但还尚未提交
     *
     * @param login               申请者
     * @param agentName           公司名称
     * @param type                升级类型
     * @param address             公司地址
     * @param cardBackPath        身份证背后
     * @param cardFrontPath       身份前面
     * @param businessLicensePath 可选的营业执照
     * @return 申请信息
     */
    @Transactional
    PromotionRequest initRequest(Login login, String agentName, int type, Address address, String cardBackPath
            , String cardFrontPath, String businessLicensePath) throws IOException;

    /**
     * 提交这个申请，让管理员可见
     *
     * @param request 申请
     */
    @Transactional
    void submitRequest(PromotionRequest request);

    MarketUserNoticeType getPaySuccessMessage();

    void registerNotices(WechatNoticeHelper wechatNoticeHelper);

    /**
     * 升级至经销商的价格
     *
     * @return 价格
     */
    BigDecimal getPriceFor1();
}
