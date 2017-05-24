package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.repository.ContactWayRepository;
import cn.lmjia.market.core.repository.CustomerRepository;
import cn.lmjia.market.core.repository.LoginRepository;
import cn.lmjia.market.core.repository.ManagerRepository;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.service.LoginService;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.service.VerificationCodeService;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.standard.repository.StandardWeixinUserRepository;
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
    @Autowired
    private PublicAccount publicAccount;
    @Autowired
    private StandardWeixinUserRepository standardWeixinUserRepository;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @Autowired
    private CustomerRepository customerRepository;

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

    @Override
    public Login asWechat(String openId) {
        return loginRepository.findOne((root, query, cb) -> cb.equal(root.get("wechatUser").get("openId"), openId));
    }

    @Override
    public void bindWechat(String loginName, String rawPassword, String openId) {
        Login login = getEnableLogin(loginName);
        if (!passwordEncoder.matches(rawPassword, login.getPassword())) {
            throw new IllegalArgumentException();
        }

        login.setWechatUser(standardWeixinUserRepository.findByOpenId(openId));
    }

    private Login getEnableLogin(String loginName) {
        Login login = loginRepository.findByLoginName(loginName);
        if (login == null || !login.isEnabled()
                || !login.isAccountNonExpired()
                || !login.isAccountNonLocked()
                || !login.isCredentialsNonExpired())
            throw new IllegalArgumentException();
        return login;
    }

    @Override
    public void bindWechatWithCode(String mobile, String code, String openId) throws IllegalVerificationCodeException {
        verificationCodeService.verify(mobile, code, loginVerificationType());
        Login login = getEnableLogin(mobile);
        login.setWechatUser(standardWeixinUserRepository.findByOpenId(openId));
    }

    @Override
    public AgentLevel lowestAgentLevel(Login who) {
        List<AgentLevel> allAgent = agentLevelRepository.findByLogin(who);

        if (allAgent.isEmpty()) {
            Customer customer = customerRepository.findByLogin(who);
            if (customer == null)
                throw new IllegalStateException("找不到" + who + "所处的经销商");
            return customer.getAgentLevel();
        }

        // 排除掉所有
        AgentLevel[] all = new AgentLevel[allAgent.size()];
        allAgent.toArray(all);

        for (AgentLevel agentLevel : all) {
            // 有人以agentLevel为上级?
            allAgent.stream()
                    .filter(level
                            -> level.getSuperior() == agentLevel)
                    .findAny()
                    .ifPresent(level
                            -> allAgent.remove(agentLevel));
        }

        return allAgent.get(0);
    }
}
