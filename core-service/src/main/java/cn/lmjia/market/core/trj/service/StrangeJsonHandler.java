package cn.lmjia.market.core.trj.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author CJ
 */
class StrangeJsonHandler<T> extends AbstractResponseHandler<T> {

    private static final Log log = LogFactory.getLog(StrangeJsonHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> type;

    StrangeJsonHandler() {
        this(null);
    }

    StrangeJsonHandler(Class<T> clazz) {
        type = clazz;
    }

    @Override
    public T handleEntity(HttpEntity entity) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        StreamUtils.copy(entity.getContent(), buffer);
        if (log.isDebugEnabled()) {
            log.debug(StreamUtils.copyToString(new ByteArrayInputStream(buffer.toByteArray()), Charset.forName("UTF-8")));
        }
        JsonNode root = objectMapper.readTree(new ByteArrayInputStream(buffer.toByteArray()));

        final JsonNode flag = root.get("success");
        if ((flag.isNumber() && flag.intValue() == 0)
                || (flag.isTextual() && flag.textValue().equals("0")))
            throw new IllegalStateException(root.get("message").asText());
        if ((flag.isBoolean() && !flag.booleanValue())
                || (flag.isTextual() && flag.textValue().equalsIgnoreCase("false")))
            throw new IllegalStateException(root.get("message").asText());

        if (type == null || type == Void.class)
            return null;
        JsonNode data = root.get("data");

        if (type.isArray() && !data.isArray()) {
            return objectMapper.readValue(data.asText(), type);
        }

        return objectMapper.readValue(objectMapper.treeAsTokens(data), type);
    }
}
