package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.VerificationType;
import me.jiangcai.user.notice.User;
import me.jiangcai.wx.standard.entity.StandardWeixinUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author CJ
 */
public interface LoginService extends UserDetailsService {

    /**
     * @return 用于登录的验证码
     */
    default VerificationType loginVerificationType() {
        return new VerificationType() {
            @Override
            public int id() {
                return 1;
            }

            @Override
            public String message(String code) {
                return "登录短信验证码为：" + code + "；请勿泄露。";
            }
        };
    }

    /**
     * @return 用于注册的验证码
     */
    default VerificationType registerVerificationType() {
        return new VerificationType() {
            @Override
            public int id() {
                return 2;
            }

            @Override
            public String message(String code) {
                return "注册短信验证码为：" + code + "；请勿泄露。";
            }
        };
    }

    /**
     * @return 所有管理员
     */
    @Transactional(readOnly = true)
    List<Manager> managers();

    /**
     * 更新密码
     *
     * @param login       一个登录
     * @param rawPassword 明文密码  @return 已被保存的登录
     */
    @Transactional
    default <T extends Login> T password(T login, String rawPassword) {
        return password(login, null, rawPassword);
    }

    /**
     * 更新密码
     *
     * @param login       一个登录
     * @param loginName   可选的新登录名；只有非null才会去应用
     * @param rawPassword 明文密码  @return 已被保存的登录
     */
    @Transactional
    <T extends Login> T password(T login, String loginName, String rawPassword);

    Login get(long id);

    /**
     * 新增普通登录
     *
     * @param type        类型
     * @param username    登录名
     * @param guide       引导者
     * @param rawPassword 明文密码
     * @return 新增身份
     */
    @Transactional
    <T extends Login> T newLogin(Class<T> type, String username, Login guide, String rawPassword);

    /**
     * 新增普通登录
     *
     * @param type       类型
     * @param guide      引导者
     * @param weixinUser 微信用户
     * @return 新增身份
     */
    @Transactional
    <T extends Login> T newLogin(Class<T> type, Login guide, StandardWeixinUser weixinUser);

    /**
     * 手机号码可用性校验
     * 应当同时保证可在Login中作为登录名，也可以作为mobile放在ContactWay
     *
     * @param mobile 号码
     * @return true for 可用
     */
    @Transactional(readOnly = true)
    boolean mobileValidation(String mobile);

    /**
     * 应当在数据约束上保证该返回值不可为多
     *
     * @param openId 微信的openId
     * @return null;如果尚未跟这个微信号产生关联
     */
    Login asWechat(String openId);

    /**
     * 绑定指定Login和openId
     *
     * @throws IllegalArgumentException 密码或者用户名错误
     */
    @Transactional
    void bindWechat(String loginName, String rawPassword, String openId);

    /**
     * @throws IllegalVerificationCodeException - 验证码无效
     * @see com.huotu.verification.service.VerificationCodeService#verify(String, String, VerificationType)
     */
    @Transactional
    void bindWechatWithCode(String mobile, String code, String openId) throws IllegalVerificationCodeException;

    /**
     * 这个身份相关的经销商；如果登录者并非任何体系内的代理商；则以客户关系查找它所属的经销商
     *
     * @param who 身份
     * @return 经销商
     */
    @Transactional(readOnly = true)
    AgentLevel lowestAgentLevel(Login who);

    /**
     * @param loginName 登录名
     * @return null or 身份
     */
    @Transactional(readOnly = true)
    Login byLoginName(String loginName);

    /**
     * @param loginName 登录名
     * @return 是否为管理员
     */
    @Transactional(readOnly = true)
    boolean isManager(String loginName);

    /**
     * @param login 身份
     * @return 是否为正式用户，依据为是否为代理商，是否成功下单并且支付
     */
    @Transactional(readOnly = true)
    boolean isRegularLogin(Login login);

    /**
     * 解除登录的微信号绑定
     *
     * @param loginName 登录名
     */
    @Transactional
    void unbindWechat(String loginName);

    /**
     * @param input 用户
     * @return 微信模板消息接收者
     */
    Collection<User> toWechatUser(Collection<? extends Login> input);
}
