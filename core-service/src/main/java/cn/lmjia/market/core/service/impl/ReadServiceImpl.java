package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.define.Money;
import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.SystemService;
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
    @Autowired
    private SystemService systemService;

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
    public Address addressFor(Object principal) {
        if (principal == null)
            return null;
        final Login login = toLogin(principal);
        ContactWay contactWay = loginRepository.getOne(login.getId()).getContactWay();
        return contactWay.getAddress();
    }

    private Login toLogin(Object principal) {
        final Login login;
        if (principal instanceof AgentLevel) {
            login = ((AgentLevel) principal).getLogin();
        } else
            login = (Login) principal;
        return login;
    }

    @Override
    public String percentage(BigDecimal input) {
        return NumberUtils.normalPercentage(input);
    }

    @Override
    public Money currentBalance(Object principal) {
        Login login = toLogin(principal);
        login = loginRepository.getOne(login.getId());
        return new Money(login.getCommissionBalance());
    }

    @Override
    public String avatarFor(Object principal) {
        Login login = toLogin(principal);
        login = loginRepository.getOne(login.getId());
        if (login.getWechatUser() != null)
            return login.getWechatUser().getHeadImageUrl();
        return systemService.toUrl("/wechat-resource/assets/img/avatar.jpg");
    }
}
