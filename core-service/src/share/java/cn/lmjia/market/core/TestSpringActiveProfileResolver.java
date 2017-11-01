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
        String[] activeProfiles = super.resolve(testClass);
        final String activeProfilesFromProperty = System.getProperty("spring.profiles.active");
        final String activeProfilesFromEnv = System.getenv("spring.profiles.active");
        if (!StringUtils.isEmpty(activeProfilesFromProperty)) {
            activeProfiles = ArrayUtils.addAll(activeProfiles, activeProfilesFromProperty.split(","));
        }
        if(!StringUtils.isEmpty(activeProfilesFromEnv)){
            activeProfiles =  ArrayUtils.addAll(activeProfiles, activeProfilesFromEnv.split(","));
        }
        return activeProfiles;
    }
}
