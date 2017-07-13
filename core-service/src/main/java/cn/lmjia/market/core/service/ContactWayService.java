package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.Address;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import java.io.IOException;

/**
 * @author CJ
 */
public interface ContactWayService {

    /**
     * @param login           身份的from
     * @param criteriaBuilder cb
     * @return 表示地址的path
     */
    static Path<Address> AddressForLogin(From<?, Login> login, CriteriaBuilder criteriaBuilder) {
        return login.get("contactWay").get("address");
    }

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

    /**
     * 更新地址
     *
     * @param login   身份
     * @param address 地址
     */
    @Transactional
    ContactWay updateAddress(Login login, Address address);

    /**
     * 更新身份证证件照
     * 资源将被转存
     *
     * @param login                       身份
     * @param frontResourcePath           个人信息面
     * @param backResourcePath            国徽面
     * @param businessLicenseResourcePath 可选的营业执照
     */
    @Transactional
    ContactWay updateIDCardImages(Login login, String frontResourcePath, String backResourcePath
            , String businessLicenseResourcePath) throws IOException;
}
