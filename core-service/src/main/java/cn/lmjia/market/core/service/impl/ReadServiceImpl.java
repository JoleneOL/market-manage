package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.service.ReadService;
import me.jiangcai.lib.seext.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * @author CJ
 */
@Service("readService")
public class ReadServiceImpl implements ReadService {

    @Autowired
    private LoginRepository loginRepository;

    @Override
    public String mobileFor(Object principal) {
        if (principal == null)
            return "";
        ContactWay contactWay = loginRepository.getOne(((Login) principal).getId()).getContactWay();
        if (contactWay == null)
            return "";
        if (StringUtils.isEmpty(contactWay.getMobile()))
            return "";
        return contactWay.getMobile();
    }

    @Override
    public String nameForPrincipal(Object principal) {
        if (principal == null)
            return "";
        final Login login = (Login) principal;
        ContactWay contactWay = loginRepository.getOne(login.getId()).getContactWay();
        if (contactWay == null)
            return login.getLoginName();
        if (StringUtils.isEmpty(contactWay.getName()))
            return login.getLoginName();
        return contactWay.getName();
    }

    @Override
    public String percentage(BigDecimal input) {
        return NumberUtils.normalPercentage(input);
    }
}
