package cn.lmjia.market.core.config;

import cn.lmjia.market.core.Version;
import com.huotu.verification.VerificationCodeConfig;
import me.jiangcai.lib.jdbc.JdbcSpringConfig;
import me.jiangcai.lib.resource.ResourceSpringConfig;
import me.jiangcai.lib.spring.logging.LoggingConfig;
import me.jiangcai.lib.sys.SystemStringConfig;
import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.lib.upgrade.UpgradeSpringConfig;
import me.jiangcai.lib.upgrade.VersionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 核心服务 加载者
 *
 * @author CJ
 */
@Configuration
@Import({ResourceSpringConfig.class, UpgradeSpringConfig.class, JdbcSpringConfig.class
        , VerificationCodeConfig.class
//        , GAASpringConfig.class
//        , NoticeSpringConfig.class
        , SystemStringConfig.class
        , LoggingConfig.class})
class CommonConfig {

    @Autowired
    private SystemStringService systemStringService;

    @Bean
    @SuppressWarnings("unchecked")
    public VersionInfoService versionInfoService() {
        final String versionKey = "version.database";
        return new VersionInfoService() {

            @Override
            public <T extends Enum> T currentVersion(Class<T> type) {
                String value = systemStringService.getSystemString(versionKey, String.class, null);
                if (value == null)
                    return null;
                return (T) Version.valueOf(value);
            }

            @Override
            public <T extends Enum> void updateVersion(T currentVersion) {
                systemStringService.updateSystemString(versionKey, currentVersion.name());
            }
        };
    }


}
