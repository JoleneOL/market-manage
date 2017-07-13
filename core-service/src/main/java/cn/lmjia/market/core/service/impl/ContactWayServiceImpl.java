package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.service.ContactWayService;
import me.jiangcai.lib.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author CJ
 */
@Service
public class ContactWayServiceImpl implements ContactWayService {

    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private ResourceService resourceService;

    @Override
    public ContactWay updateMobile(Login login, String mobile) {
        return updateContactWay(login, contactWay -> contactWay.setMobile(mobile));
    }

    private ContactWay updateContactWay(Login login, Consumer<ContactWay> contactWayConsumer) {
        if (login.getId() != null)
            login = loginRepository.getOne(login.getId());

        if (login.getContactWay() == null) {
            login.setContactWay(new ContactWay());
        }
        contactWayConsumer.accept(login.getContactWay());
        return login.getContactWay();
    }

    @Override
    public ContactWay updateName(Login login, String name) {
        return updateContactWay(login, contactWay -> contactWay.setName(name));
    }

    @Override
    public ContactWay updateAddress(Login login, Address address) {
        return updateContactWay(login, contactWay -> contactWay.setAddress(address));
    }

    @Override
    public ContactWay updateIDCardImages(Login login, String frontResourcePath, String backResourcePath
            , String businessLicenseResourcePath)
            throws IOException {
        String id = UUID.randomUUID().toString();
        String frontPath = "contact/" + id + "/front." + ext(frontResourcePath);
        String backPath = "contact/" + id + "/back." + ext(backResourcePath);
        resourceService.moveResource(frontPath, frontResourcePath);
        resourceService.moveResource(backPath, backResourcePath);
        String businessLicensePath;
        if (!StringUtils.isEmpty(businessLicenseResourcePath)) {
            businessLicensePath = "contact/" + id + "/businessLicense." + ext(businessLicenseResourcePath);
            resourceService.moveResource(businessLicensePath, businessLicenseResourcePath);
        } else
            businessLicensePath = null;
        return updateContactWay(login, contactWay -> {
            contactWay.setFrontImagePath(frontPath);
            contactWay.setBackImagePath(backPath);
            if (businessLicensePath != null)
                contactWay.setBusinessLicensePath(businessLicensePath);
        });
    }

    private String ext(String path) {
        int index = path.lastIndexOf(".");
        return path.substring(index + 1, path.length());
    }
}
