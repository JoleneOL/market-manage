package cn.lmjia.market.core.aop;

/**
 * 支持成为一个多重业务锁
 * Created by helloztt on 2017-01-09.
 */
public interface MultipleBusinessLocker {

    /**
     * @return 业务锁
     */
    Object[] toLock();
}
