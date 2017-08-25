package cn.lmjia.market.manage.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 单元测试时对时间控件复制可能会用到
 * Created by helloztt on 2017/8/20.
 */
public class LocalDateUtils {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String formatLocalDate(LocalDate localDate) {
        return dateFormatter.format(localDate);
    }

    public String formatLocalDate(LocalDate localDate, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(localDate);
    }

    public String formatLocalDateTime(LocalDateTime localDateTime) {
        return formatter.format(localDateTime);
    }

    public String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }
}
