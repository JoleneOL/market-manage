package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.service.ContactWayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * @author CJ
 */
@Service
public class ContactWayServiceImpl implements ContactWayService {

    @Autowired
    private LoginRepository loginRepository;

    @Override
    public ContactWay updateMobile(Login login, String mobile) {
        return updateContactWay(login, contactWay -> contactWay.setMobile(mobile));
    }

    private ContactWay updateContactWay(Login login, Consumer<ContactWay> contactWayConsumer) {
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
}
