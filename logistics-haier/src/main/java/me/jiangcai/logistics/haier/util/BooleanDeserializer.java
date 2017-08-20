package me.jiangcai.logistics.haier.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * 0,1 到 boolean
 *
 * @author CJ
 */
public class BooleanDeserializer extends StdDeserializer<Boolean> {

    public BooleanDeserializer() {
        super(Integer.class);
    }

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        int x = this._parseIntPrimitive(p, ctxt);
        return x == 1;
    }
}
