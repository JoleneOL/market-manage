package me.jiangcai.logistics.haier.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author CJ
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.CHINA);

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
//        ctxt.getAnnotationIntrospector().findFormat(new AnnotatedField());
        String time = p.readValueAs(String.class);
        return LocalDateTime.parse(time, format);
    }
}
