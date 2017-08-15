package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.service.ContactWayService;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.seext.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static final Log log = LogFactory.getLog(ContactWayServiceImpl.class);
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private ResourceService resourceService;

    @Override
    public ContactWay updateMobile(Login login, String mobile) {
        return updateContactWay(login, contactWay -> contactWay.setMobile(mobile));
    }

    private ContactWay updateContactWay(Login loginInput, Consumer<ContactWay> contactWayConsumer) {
        Login login;
        if (loginInput.getId() != null) {
            login = loginRepository.getOne(loginInput.getId());
        } else login = loginInput;
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
        String frontPath = "contact/" + id + "/front." + FileUtils.fileExtensionName(frontResourcePath);
        String backPath = "contact/" + id + "/back." + FileUtils.fileExtensionName(backResourcePath);
        resourceService.moveResource(frontPath, frontResourcePath);
        resourceService.moveResource(backPath, backResourcePath);
        String businessLicensePath;
        if (!StringUtils.isEmpty(businessLicenseResourcePath)) {
            businessLicensePath = "contact/" + id + "/businessLicense." + FileUtils.fileExtensionName(businessLicenseResourcePath);
            resourceService.moveResource(businessLicensePath, businessLicenseResourcePath);
        } else
            businessLicensePath = null;
        return updateContactWay(login, contactWay -> {

            try {
                if (!StringUtils.isEmpty(contactWay.getFrontImagePath()))
                    resourceService.deleteResource(contactWay.getFrontImagePath());
                if (!StringUtils.isEmpty(contactWay.getBackImagePath()))
                    resourceService.deleteResource(contactWay.getBackImagePath());
                if (businessLicensePath != null && !StringUtils.isEmpty(contactWay.getBusinessLicensePath()))
                    resourceService.deleteResource(contactWay.getBusinessLicensePath());
            } catch (IOException e) {
                log.trace("", e);
            }
            contactWay.setFrontImagePath(frontPath);
            contactWay.setBackImagePath(backPath);
            if (businessLicensePath != null)
                contactWay.setBusinessLicensePath(businessLicensePath);
        });
    }

}
