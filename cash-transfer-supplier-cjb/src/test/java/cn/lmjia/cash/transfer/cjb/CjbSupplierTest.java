package cn.lmjia.cash.transfer.cjb;

import cn.lmjia.cash.transfer.CashReceiver;
import cn.lmjia.cash.transfer.EntityOwner;
import cn.lmjia.cash.transfer.OwnerAccount;
import cn.lmjia.cash.transfer.service.CashTransferService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.swing.text.html.parser.Entity;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebAppConfiguration
@ContextConfiguration(classes = CjbConfig.class)
public class CjbSupplierTest {

    @Autowired
    CashTransferService cashTransferService;

    @Test
    public void go(){
        //需要主体.测试帐号
        EntityOwner entityOwner = getEntityOwner();
        //需要提现信息,随便写一个好了


    }

    public EntityOwner getEntityOwner(){

        return new EntityOwner(){

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
                        Map<String,String> message = new HashMap<>();
                        message.put("cid","1100343164");
                        message.put("userId","chenlong");
                        message.put("userPass","a11111");
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
    public CashReceiver getCashReceiver(){
        return new CashReceiver() {
            @Override
            public Long getId() {
                return 1l;
            }

            @Override
            public String getAccountNum() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getBankDesc() {
                return null;
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
                return null;
            }

            @Override
            public String getPurpose() {
                return null;
            }

            @Override
            public String getMemo() {
                return null;
            }
        };
    }
}
