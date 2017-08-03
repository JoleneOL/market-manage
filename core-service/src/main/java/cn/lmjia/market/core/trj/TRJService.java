package cn.lmjia.market.core.trj;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.trj.AuthorisingInfo;
import cn.lmjia.market.core.event.MainOrderFinishEvent;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.event.OrderPaySuccess;
import org.apache.http.NameValuePair;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 投融家相关服务，我们也认可它是一种支付方式
 *
 * @author CJ
 */
public interface TRJService extends PaymentForm {

    /**
     * 分期名称
     */
    String ChannelName = "投融家分期";

    /**
     * 添加一个有效按揭码
     *
     * @param authorising
     * @param idNumber
     */
    @Transactional
    void addAuthorisingInfo(String authorising, String idNumber);

    void submitOrderCompleteRequest(String authorising, Number orderId, String address, String installer
            , String installCompany, String mobile, String installTime, Number amount, String resourcePath) throws IOException;

    void deliverUpdate(Number orderId, String authorising, String deliverCompany, String deliverStore
            , Number stockQuantity, String shipmentTime, String deliverTime, String name, String mobile, String address
            , String orderTime) throws IOException;

    void submitOrderInfo(String authorising, Number orderId, String name, String idNumber, String mobile
            , String goodCode, String goodName, Number amount, String dueAmount, String address, String orderTime
            , Number recommendCode) throws IOException;

    String sign(List<NameValuePair> list);

    @EventListener(OrderPaySuccess.class)
    void paySuccess(OrderPaySuccess event);

    /**
     * 检查可用的按揭码
     *
     * @param authorising
     * @param idNumber
     * @return
     * @throws InvalidAuthorisingException
     */
    @Transactional(readOnly = true)
    AuthorisingInfo checkAuthorising(String authorising, String idNumber) throws InvalidAuthorisingException;

    /**
     * 物流信息更新
     *
     * @param orderId        订单号
     * @param deliverCompany 物流公司
     * @param deliverStore   物流仓库
     * @param stockQuantity  之后的剩余库存
     * @param shipmentTime   发货时间
     * @param deliverTime    送达时间
     */
    void deliverUpdate(long orderId, String deliverCompany, String deliverStore, int stockQuantity
            , LocalDate shipmentTime, LocalDate deliverTime);

    /**
     * @param authorising 按揭码
     * @return 寻找这个按揭码所对应的订单；可能为null
     */
    @Transactional(readOnly = true)
    MainOrder findOrder(String authorising);

    @EventListener(MainOrderFinishEvent.class)
    void orderSuccess(MainOrderFinishEvent event);

    /**
     * 发送消息给客服，让他们知道需要提起信审了
     *
     * @param order
     * @param message
     */
    void sendCheckWarningToCS(MainOrder order, String message);

    /**
     * 提起信审申请
     *
     * @param order          订单
     * @param installer      安装人员
     * @param installCompany 安装公司
     * @param mobile         安装人员手机
     * @param installTime    安装时间
     * @param resourcePath   可选的附件资源；
     */
    @Transactional
    void submitOrderCompleteRequest(MainOrder order, String installer, String installCompany, String mobile
            , LocalDateTime installTime, String resourcePath);
}
