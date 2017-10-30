package cn.lmjia.cash.transfer.cjb;

import cn.lmjia.cash.transfer.*;
import cn.lmjia.cash.transfer.cjb.message.Fox;
import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author lxf
 */
public interface CjbSupplier extends CashTransferSupplier {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter fotmatterYear = DateTimeFormatter.ofPattern("yyyyMMdd");
    ObjectMapper objectMapper = new ObjectMapper();
    XmlMapper xmlMapper = new XmlMapper();

    /**
     *
     * @param requestMessage  请求报文对象
     * @param interBank 是否行内银行
     * @param local 是否同城
     * @return
     * @throws JsonProcessingException 对象转换报文出错
     * @throws BadAccessException 与银行访问出错
     */
    Fox sendRequest(Fox requestMessage, Boolean interBank, Boolean local) throws JsonProcessingException, BadAccessException;

    /**
     *
     * @param requestMessage 请求报文对象
     * @return
     * @throws JsonProcessingException 对象转换报文出错
     * @throws BadAccessException 与银行访问出错
     */
    Fox sendRequest(Fox requestMessage) throws JsonProcessingException, BadAccessException;

}
