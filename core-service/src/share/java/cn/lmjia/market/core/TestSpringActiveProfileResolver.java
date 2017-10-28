package cn.lmjia.market.core;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.test.context.support.DefaultActiveProfilesResolver;
import org.springframework.util.StringUtils;


/**
 * Created by helloztt on 2017/10/29.
 */
public class TestSpringActiveProfileResolver extends DefaultActiveProfilesResolver {
    @Override
    public String[] resolve(Class<?> testClass) {
        final String[] activeProfiles = super.resolve(testClass);
        final String activeProfilesFromProperty = System.getProperty("spring.profiles.active");
        if (!StringUtils.isEmpty(activeProfilesFromProperty)) {
            return ArrayUtils.addAll(activeProfiles, activeProfilesFromProperty.split(","));
        }
        return activeProfiles;
    }
}
