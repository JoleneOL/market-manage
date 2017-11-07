package cn.lmjia.cash.transfer;

/**
 * 业务主，企业法人或者一个自然人都可以
 *
 * @author CJ
 */
public interface EntityOwner {
    /**
     * @param supplierName 选择付款的银行.也是选择供应商
     * @return 当前主体的对应银行的账户信息
     */
    OwnerAccount getOwnerAccount(String supplierName);

    /**
     * @return 获取主体的名称,可以是公司名,法人,自然人名
     */
    String getOwnerName();
}
