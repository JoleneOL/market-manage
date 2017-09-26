package cn.lmjia.market.core.util;

import java.time.LocalDate;

/**
 * @Author: lixuefeng
 */

public class TimeUtil {
    private TimeUtil() {}

    /**
     * 比对给定时间是否是当月的某一天时间.
     *
     * @param date     给定时间
     * @param before   当月某一天
     * @return
     */
    public static boolean beforeTheDate(LocalDate date, int before) {
        LocalDate fifthDay = LocalDate.now().withDayOfMonth(before);
        return date.isBefore(fifthDay);
    }

    /**
     * 比对给定时间是都在某个时间范围内
     *
     * @param date    给定时间
     * @param start   开始时间
     * @param end     截止时间
     * @return
     */
    public static boolean timeFrame(LocalDate date, int start, int end) {
        LocalDate startTime = LocalDate.now().withDayOfMonth(start);
        LocalDate endTime = LocalDate.now().withDayOfMonth(end);
        return date.isAfter(startTime) && date.isBefore(endTime);
    }

}
