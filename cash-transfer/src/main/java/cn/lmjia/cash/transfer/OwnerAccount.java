package cn.lmjia.cash.transfer;

import java.util.Map;

/**
 * 付款人帐号
 */
public interface OwnerAccount {

    /**
     * @return 付款账号
     */
    String getAccountNum();

    /**
     * @return 付款人姓名
     */
    String getName();

    /**
     * @return 开户行
     */
    String getBankDesc();

    /**
     * @return 汇款城市
     */
    String getCity();

    /**
     * @return 访问供应商的登录信息. cid  客户号,  userId 登录名, userPass 密码.
     */
    Map<String,String> getLoginInformation();
}
