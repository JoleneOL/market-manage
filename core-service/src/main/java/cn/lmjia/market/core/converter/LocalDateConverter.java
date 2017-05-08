package cn.lmjia.market.core.converter;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author CJ
 */
@Component
public class LocalDateConverter implements Formatter<LocalDate> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.CHINA);

    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        if (StringUtils.isEmpty(text))
            return null;
        return LocalDate.from(formatter.parse(text));
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        if (object == null)
            return null;
        return formatter.format(object);
    }
}
