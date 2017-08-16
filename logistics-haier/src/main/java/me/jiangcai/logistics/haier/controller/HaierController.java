package me.jiangcai.logistics.haier.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.jiangcai.logistics.haier.HaierSupplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Controller
public class HaierController {

    private static final Log log = LogFactory.getLog(HaierController.class);
    @Autowired
    private HaierSupplier haierSupplier;


    @PostMapping("/_haier_callback")
    public ResponseEntity change(@RequestBody String requestBody) throws JsonProcessingException {
        Map<String, String> requestData = Stream.of(requestBody.split("&"))
                .map((Function<String, NameValuePair>) s -> {
                    int index = s.indexOf("=");
                    try {
                        return new BasicNameValuePair(s.substring(0, index)
                                , URLDecoder.decode(s.substring(index + 1, s.length()), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new InternalError(e);
                    }
                })
                .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));

        String businessType = requestData.get("butype");
        String source = requestData.get("source");
        String contentType = requestData.get("type");
        String sign = requestData.get("sign");
        String content = requestData.get("content");

        Map<String, Object> result = new HashMap<>();
        try {
            final Object event = haierSupplier.event(businessType, source, contentType, sign, content);
            if (event != null)
                result.put("response", event);
            result.put("flag", "T");
            result.put("msg", "Ok");
        } catch (Exception ex) {
            log.trace("failed Haier Event", ex);
            result.put("flag", "F");
            result.put("msg", ex.getLocalizedMessage());
        }


        if (contentType.equalsIgnoreCase("xml")) {
            final String request = HaierSupplier.xmlMapper
                    .writer()
                    .withRootName("request")
                    .writeValueAsString(result);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/xml; charset=UTF-8"))
                    .body(request.replaceAll(" xmlns=\"\"", ""));
        }
        if (contentType.equalsIgnoreCase("json")) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(HaierSupplier.objectMapper.writeValueAsString(result));
        }
        // request
        // flag
        // msg
        // response
        return null;
    }

}
