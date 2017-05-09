package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.repository.ContactWayRepository;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.repository.ManagerRepository;
import cn.lmjia.market.core.service.LoginService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CJ
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static final Log log = LogFactory.getLog(LoginServiceImpl.class);

    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private ContactWayRepository contactWayRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Login login = loginRepository.findByLoginName(username);
        if (login == null)
            throw new UsernameNotFoundException(username);
        return login;
    }

    @Override
    public List<Manager> managers() {
        return managerRepository.findAll();
    }

    @Override
    public <T extends Login> T password(T login, String rawPassword) {
        login.setPassword(passwordEncoder.encode(rawPassword));
        return loginRepository.save(login);
    }

    @Override
    public Login get(long id) {
        return loginRepository.getOne(id);
    }

    @Override
    public Login newLogin(String username, Login guide, String rawPassword) {
        Login login = new Login();
        login.setLoginName(username);
        login.setGuideUser(guide);
        return password(login, rawPassword);
    }

    @Override
    public boolean mobileValidation(String mobile) {
        if (loginRepository.count((root, query, cb) -> cb.equal(root.get("loginName"), mobile)) > 0)
            return false;
        if (contactWayRepository.count((root, query, cb) -> cb.equal(root.get("mobile"), mobile)) > 0)
            return false;
        if (log.isTraceEnabled())
            log.trace("通过手机可用性检测:" + mobile);
        return true;
    }
}
