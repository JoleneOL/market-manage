package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.service.LoginService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class LoginServiceImpl implements LoginService{
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
