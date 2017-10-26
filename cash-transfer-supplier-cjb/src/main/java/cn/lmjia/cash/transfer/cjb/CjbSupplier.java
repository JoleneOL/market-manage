package cn.lmjia.cash.transfer.cjb;

import cn.lmjia.cash.transfer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * @author lxf
 */
public interface CjbSupplier extends CashTransferSupplier {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter fotmatterYear = DateTimeFormatter.ofPattern("yyyyMMdd");
    ObjectMapper objectMapper = new ObjectMapper();
    XmlMapper xmlMapper = new XmlMapper();
}
