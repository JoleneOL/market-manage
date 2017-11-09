package cn.lmjia.market.core.service;


import cn.lmjia.cash.transfer.model.CashTransferResult;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest;
import cn.lmjia.market.core.util.Utils;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.VerificationType;
import me.jiangcai.lib.notice.Content;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface WithdrawService {

    /**
     * 新创建提现，新申请总是没法马上开始工作的。
     *
     * @param who              提现者
     * @param payee            收款人
     * @param account          收款账号
     * @param bank             开户行
     * @param mobile           收款人电话
     * @param amount           提现金额
     * @param logisticsCode    物流单号
     * @param logisticsCompany 物流公司
     * @return 新创建的提现
     */
    @Transactional
    WithdrawRequest withdrawNew(Login who, String payee, String account, String bank, String mobile, BigDecimal amount
            , String logisticsCode, String logisticsCompany);

    /**
     * @return 用于提现校验的验证码
     */
    default VerificationType withdrawVerificationType() {
        return new VerificationType() {
            @Override
            public int id() {
                return 3;
            }

            @Override
            public String message(String code) {
                return "提现校验短信验证码为：" + code + "；请勿泄露。";
            }

            @Override
            public Content generateContent(String code) {
                return Utils.generateCodeContent(this, code, "SMS_94675070");
            }
        };
    }

    /**
     * @return 无发票的扣税率
     */
    BigDecimal getCostRateForNoInvoice();

    /**
     * 提交指定用户最后的申请
     *
     * @param login 用户
     * @param code  验证码
     * @throws IllegalVerificationCodeException - 验证码无效
     * @see com.huotu.verification.service.VerificationCodeService#verify(String, String, VerificationType)
     */
    @Transactional
    void submitRequest(Login login, String code) throws IllegalVerificationCodeException;

    @Transactional(readOnly = true)
    WithdrawRequest get(long id);

    /**
     * 拒绝特定提现申请
     *
     * @param manager   处理人
     * @param requestId 请求
     * @param comment   留言
     */
    @Transactional
    void reject(Manager manager, long requestId, String comment);

    /**
     * 通过特定提现申请
     * @param manager                 处理人
     * @param requestId               请求
     * @param result                  封装的从银行接收的结果数据.
     */
    @Transactional
    void approval(Manager manager, long requestId, CashTransferResult result);

    /**
     * 自动转账成功时,将这个提现请求的状态修改掉.
     *
     * @param withdrawRequestId 成功的提现上申请
     * @param processingTime 供应商(银行)处理的时间.
     */
    @Transactional
    void automaticIsSuccessful(long withdrawRequestId, LocalDateTime processingTime);
}
