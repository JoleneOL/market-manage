package me.jiangcai.logistics.haier.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import me.jiangcai.logistics.exception.SupplierException;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;

import java.io.IOException;

/**
 * @author CJ
 */
public class ResponseHandler extends AbstractResponseHandler<Void> {

    private static final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public Void handleEntity(HttpEntity entity) throws IOException {
        JsonNode node = xmlMapper.readTree(entity.getContent());
        if (node.get("flag").asText().equalsIgnoreCase("T"))
            return null;
        throw new SupplierException(node.get("msg").asText());
    }
}
