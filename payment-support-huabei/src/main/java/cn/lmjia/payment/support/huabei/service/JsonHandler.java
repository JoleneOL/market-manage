package cn.lmjia.payment.support.huabei.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;

import java.io.IOException;
import java.util.Map;

/**
 * @author CJ
 */
public class JsonHandler extends AbstractResponseHandler<Map<String, Object>> {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleEntity(HttpEntity entity) throws IOException {
        //noinspection unchecked
        return objectMapper.readValue(entity.getContent(), Map.class);
    }
}
