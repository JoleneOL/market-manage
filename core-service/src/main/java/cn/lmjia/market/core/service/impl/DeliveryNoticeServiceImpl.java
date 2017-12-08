package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.service.DeliveryNoticeService;
import com.huotu.verification.service.VerificationCodeService;
import me.jiangcai.lib.notice.Content;
import me.jiangcai.lib.notice.NoticeService;
import me.jiangcai.lib.notice.NoticeSupplier;
import me.jiangcai.lib.notice.To;
import me.jiangcai.logistics.entity.ManuallyOrder;
import me.jiangcai.logistics.entity.StockShiftUnit;
import me.jiangcai.logistics.event.DeliveryGoodsSuccessEvent;
import me.jiangcai.logistics.haier.entity.HaierOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeliveryNoticeServiceImpl implements DeliveryNoticeService {

    @Autowired
    private NoticeService noticeService;
    private final String noticeSupplier;

    public DeliveryNoticeServiceImpl(Environment environment){
        noticeSupplier = environment.getProperty("com.huotu.notice.supplier");
    }
    @Override
    public void sendNotification(DeliveryGoodsSuccessEvent event) throws ClassNotFoundException {
        final StockShiftUnit unit = event.getUnit();
        final String transporter ;
        final String orderNumber ;
        if(unit instanceof HaierOrder){
            //减少点字数, 阿里云70个字是一条短信,多余的将多产生费用.
            transporter = "青岛日日顺物流有限公司".equals(unit.getSupplierOrganizationName()) ? "日日顺物流":unit.getSupplierOrganizationName();
            orderNumber = ((HaierOrder) unit).getOrderNumber();
        }else {
            //手动发货
            transporter = ((ManuallyOrder)unit).getSupplierCompany();
            orderNumber = ((ManuallyOrder)unit).getOrderNumber();
        }
        noticeService.send(noticeSupplier,new To() {
            @Override
            public String mobilePhone() {
                return event.getConsigneeMobile();
            }
        }, new Content() {

            @Override
            public String asText() {
                return null;
            }

            @Override
            public String signName() {
                return "利每家";
            }

            @Override
            public String templateName() {
                return "SMS_115950102";
            }

            @Override
            public Map<String, ?> templateParameters() {
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("Transporter", transporter);
                parameters.put("orderNumber", orderNumber);
                return parameters;
            }
        });
    }
}
