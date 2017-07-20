package me.jiangcai.logistics.haier;

import me.jiangcai.logistics.LogisticsSupplier;

import java.time.format.DateTimeFormatter;

/**
 * @author CJ
 */
public interface HaierSupplier extends LogisticsSupplier {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

}
