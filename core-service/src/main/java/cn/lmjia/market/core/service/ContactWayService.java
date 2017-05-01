package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CJ
 */
public interface ContactWayService {

    /**
     * 更新电话号码
     *
     * @param login  身份
     * @param mobile 电话号码
     */
    @Transactional
    ContactWay updateMobile(Login login, String mobile);

    /**
     * 更新名字
     *
     * @param login 身份
     * @param name  新的名字
     */
    @Transactional
    ContactWay updateName(Login login, String name);

}
