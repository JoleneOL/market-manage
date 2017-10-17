package cn.lmjia.cash.transfer.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * httpClient发送请求工具类
 * @author lxf
 */
public class HttpClientUtil {
    private HttpClientUtil(){};

    private static Log log = LogFactory.getLog(HttpClientUtil.class);

    /**
     *
     * @param url 请求url
     * @param Charset 请求的字符集 默认utf-8
     * @param xmlEntity 要发送的xml实体对象
     * @return
     */
    public HttpResponse postRequest(String url,String Charset,Object xmlEntity,String contentType){
        //创建请求客户端
        HttpClient httpClient = HttpClientBuilder.create().build();
        //拼装xml并组装请求
        HttpPost request = new HttpPost(url);
        //将实体类转换成xml,设置字符集

        log.info("请求报文:");
        ByteArrayEntity byteArrayEntity = null;
        try{
            //UnsupportedEncodingException e
        }catch(Exception e){
            e.printStackTrace();;
            log.error("xml写入请求编码错误!");
        }
        request.setEntity(byteArrayEntity);
        request.addHeader("Content-Type",contentType);
        HttpResponse response = null;

        try {
            //
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("网络通信错误");
        }
        return null;
    }

    public HttpResponse getRequest(){
        return null;
    }
}
