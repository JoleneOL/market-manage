package cn.lmjia.cash.transfer.cjb;

import cn.lmjia.cash.transfer.*;
import cn.lmjia.cash.transfer.cjb.service.CjbSupplierImpl;
import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.SupplierApiUpgradeException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;
import cn.lmjia.cash.transfer.service.CashTransferService;
import me.jiangcai.lib.test.SpringWebTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebAppConfiguration
@ContextConfiguration(classes = CjbConfig.class)
public class CjbSupplierTest extends SpringWebTest {

    @Autowired
    CashTransferService cashTransferService;
    @Autowired
    Environment environment;

    @Test
    public void go() throws SupplierApiUpgradeException, BadAccessException, TransferFailureException, IOException {
        //需要主体.测试帐号
        EntityOwner entityOwner = getEntityOwner();
        //需要提现信息,随便写一个好了
        CashReceiver cashReceiver = getCashReceiver();
        //兴业银行
        CjbSupplier cjbSupplier = new CjbSupplierImpl(environment);
        cashTransferService.cashTransfer(cjbSupplier, entityOwner, cashReceiver);

    }

    public EntityOwner getEntityOwner() {

        return new EntityOwner() {

            @Override
            public OwnerAccount getOwnerAccount(String supplierName) {
                return new OwnerAccount() {
                    @Override
                    public String getAccountNum() {
                        return "117010100100000177";
                    }

                    @Override
                    public String getName() {
                        return "中国民族证券有限责任公司12";
                    }

                    @Override
                    public String getBankDesc() {
                        return "兴业银行";
                    }

                    @Override
                    public String getCity() {
                        return "杭州";
                    }

                    @Override
                    public Map<String, String> getLoginInformation() {
                        Map<String, String> message = new HashMap<>();
                        message.put("cid", "1100343164");
                        message.put("userId", "chenlong");
                        message.put("userPass", "a11111");
                        return message;
                    }
                };
            }

            @Override
            public String getOwnerName() {
                return "测试主体";
            }
        };
    }

    public CashReceiver getCashReceiver() {
        return new CashReceiver() {
            @Override
            public Long getId() {
                return 3l;
            }

            @Override
            public String getWithdrawId() {
                return null;
            }

            @Override
            public String getAccountNum() {
                return "622908121000127611";
            }

            @Override
            public String getName() {
                return "汪汪";
            }

            @Override
            public String getBankDesc() {
                return "兴业银行";
            }

            @Override
            public String getBankNumber() {
                return null;
            }

            @Override
            public String getCity() {
                return null;
            }

            @Override
            public BigDecimal getWithdrawAmount() {
                return new BigDecimal(0.01);
            }

            @Override
            public String getPurpose() {
                return "测试";
            }

            @Override
            public String getMemo() {
                return "测试";
            }
        };
    }
}
