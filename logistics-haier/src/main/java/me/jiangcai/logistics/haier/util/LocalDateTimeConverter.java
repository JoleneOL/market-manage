package me.jiangcai.logistics.haier.util;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author CJ
 */
public class LocalDateTimeConverter extends StdConverter<String, LocalDateTime> {
    static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    public LocalDateTime convert(String value) {
        return LocalDateTime.parse(value, format);
    }
}
