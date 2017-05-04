package cn.lmjia.market.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.lib.resource.Resource;
import me.jiangcai.lib.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

/**
 * 资源控制器
 * 上传文件,只接受通过Ajax方式上传。
 * 上传成功之后将给出200响应,地址为资源最终地址;响应正文将携带有资源path
 * 这个资源会保存在特殊文件夹中,并在24小时内删除,所以path应该算是临时path。
 *
 * @author CJ
 */
@Controller
@RequestMapping("/resourceUpload")
public class ResourceController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ResourceService resourceService;

    /**
     * 为ckeditor而专门设置的编辑上传图片工具
     * 这里有一个很大的问题是上传以后仅仅是告知了客户端一个URL,客户端后来是否删除了 一概不得而知,所以需要一个监视的第三方工具
     * ,如果一直没有对此资源的GET请求,那么这些资源应该被删除。
     *
     * @param upload
     * @return
     */
    @RequestMapping(value = "/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> ckeditorUpload(MultipartFile upload) throws JsonProcessingException {
        try {
            try (InputStream inputStream = upload.getInputStream()) {
                String path = "watch/" + UUID.randomUUID().toString().replaceAll("-", "") + ".png";
//                ImageHelper.storeAsImage("png", resourceService, inputStream, path);
                HashMap<String, Object> body = new HashMap<>();
                body.put("uploaded", 1);
                body.put("success", true);
                body.put("fileName", path);
                body.put("newUuid", path);
                body.put("url", resourceService.getResource(path).httpUrl().toString());
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(objectMapper.writeValueAsString(body));
            }
        } catch (Exception ex) {
            HashMap<String, Object> body = new HashMap<>();
            body.put("uploaded", 0);
            body.put("success", false);
            HashMap<String, Object> error = new HashMap<>();
            error.put("message", ex.getLocalizedMessage());

            body.put("error", error);
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(objectMapper.writeValueAsString(body));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/webUploader")
    public ResponseEntity<?> webUploader(String id, MultipartFile file) throws IOException, URISyntaxException {
        // WU_FILE_0
        // 响应 包括 id 和 url
        try (InputStream inputStream = file.getInputStream()) {
            String path = uploadTempResource(inputStream, file);
            Resource resource = resourceService.getResource(path);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HashMap<String, String> data = new HashMap<>();
            data.put("id", path);
            data.put("url", resource.httpUrl().toString());
            return new ResponseEntity<>(objectMapper.writeValueAsBytes(data), httpHeaders, HttpStatus.OK);
        }
    }

    private String uploadTempResource(InputStream data) throws IOException {
        return uploadTempResource(data, null);
    }

    private String uploadTempResource(InputStream data, MultipartFile file) throws IOException {
        String path = "tmp/" + UUID.randomUUID().toString();
        if (file != null) {
            // image/png
            if (!StringUtils.isEmpty(file.getContentType())) {
                path = path + "." + MediaType.parseMediaType(file.getContentType()).getSubtype();
            } else {
                int index = file.getOriginalFilename().lastIndexOf(".");
                if (index != -1) {
                    path = path + "." + file.getOriginalFilename().substring(index + 1);
                }
            }
        }
        resourceService.uploadResource(path, data);
        return path;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> upload(MultipartFile file) throws IOException, URISyntaxException {
        try (InputStream inputStream = file.getInputStream()) {
            String path = uploadTempResource(inputStream, file);
            Resource resource = resourceService.getResource(path);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.TEXT_PLAIN);
            httpHeaders.setLocation(resource.httpUrl().toURI());
            return new ResponseEntity<>(path, httpHeaders, HttpStatus.OK);
        }
    }


    /**
     * 为fine-uploader特地准备的上传控制器
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/fine", method = RequestMethod.POST)
    @ResponseBody
    public Object fineUpload(MultipartFile file) {
        try {
            try (InputStream inputStream = file.getInputStream()) {
                String path = uploadTempResource(inputStream);
                HashMap<String, Object> body = new HashMap<>();
                body.put("success", true);
                body.put("newUuid", path);
                return body;
            }
        } catch (Exception ex) {
            HashMap<String, Object> body = new HashMap<>();
            body.put("success", false);
            body.put("error", ex.getLocalizedMessage());
            return body;
        }
    }

}
