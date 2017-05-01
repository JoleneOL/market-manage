package cn.lmjia.market.core.service;

/**
 * 获取信息服务
 * 通常都是一些非常简单的
 *
 * @author CJ
 */
public interface ReadService {

    /**
     * @param principal 身份；通常是一个{@link cn.lmjia.market.core.entity.Login}
     * @return 手机号码；或者一个空字符串
     */
    String mobileFor(Object principal);

    /**
     * @param principal 身份；通常是一个{@link cn.lmjia.market.core.entity.Login}
     * @return 名字；或者登录名
     */
    String nameForPrincipal(Object principal);

}
