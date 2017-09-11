package cn.lmjia.market.core.util;

import com.huotu.verification.VerificationType;
import me.jiangcai.lib.notice.Content;

import java.util.Collections;
import java.util.Map;

/**
 * @author CJ
 */
public class Utils {
    /**
     * 短信签名；理论上来讲应该是依赖配置的
     */
    private static final String SMS_SignName = "利每家";

    /**
     * @param code     验证码
     * @param template 模板
     * @return 生成验证码所用的短信内容
     */
    public static Content generateCodeContent(VerificationType type, String code, String template) {
        return new Content() {
            @SuppressWarnings("deprecation")
            @Override
            public String asText() {
                return type.message(code);
            }

            @Override
            public String signName() {
                return Utils.SMS_SignName;
            }

            @Override
            public String templateName() {
                return template;
            }

            @Override
            public Map<String, ?> templateParameters() {
                return Collections.singletonMap("code", code);
            }
        };
    }
}
