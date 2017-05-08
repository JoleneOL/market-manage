package cn.lmjia.market.core.converter;

import cn.lmjia.market.core.entity.support.Address;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Iterator;

/**
 * @author CJ
 */
public class AddressResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == Address.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //  把符合 // 的信息抓下来 作为地址
        String name = parameter.getParameterName();
        String basicAddress = webRequest.getParameter(name);
        if (StringUtils.isEmpty(basicAddress))
            return null;
        String[] basicAddresses = basicAddress.split("/");
        Address address = new Address();
        address.setProvince(basicAddresses[0]);
        address.setPrefecture(basicAddresses[1]);
        if (basicAddresses.length >= 3)
            address.setCounty(basicAddresses[2]);

        String otherAddressName = null;
        final Iterator<String> parameterNames = webRequest.getParameterNames();
        while (parameterNames.hasNext()) {
            String nextName = parameterNames.next();
            if (nextName.contains(name)) {
                if (otherAddressName == null)
                    otherAddressName = nextName;
                else
                    throw new IllegalArgumentException("不确定的详细地址参数名(" + otherAddressName + "," + nextName + ")");
            }
        }

        if (otherAddressName != null) {
            address.setOtherAddress(webRequest.getParameter(otherAddressName));
        }

        return address;
    }
}
