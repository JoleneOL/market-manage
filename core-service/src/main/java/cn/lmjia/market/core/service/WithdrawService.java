package cn.lmjia.market.core.service;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.VerificationType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface WithdrawService {

    /**
     * 新创建提现
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
        };
    }

    /**
     * @return 无发票的扣税率
     */
    BigDecimal getCostRateForNoInvoice();

    /**
     * @throws IllegalVerificationCodeException - 验证码无效
     * @see com.huotu.verification.service.VerificationCodeService#verify(String, String, VerificationType)
     */
    @Transactional
    void checkWithdrawCode(String mobile, String code) throws IllegalVerificationCodeException;

}
