package cn.lmjia.market.core.controller;

import cn.lmjia.market.core.CoreWebTest;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;

/**
 * 检测文件上传控制器
 *
 * @author CJ
 */
public class ResourceControllerTest extends CoreWebTest {
    @Test
    public void webUploader() throws Exception {
        try (InputStream inputStream = randomPngImageResource().getInputStream()) {
            mockMvc.perform(fileUpload("/resourceUpload/webUploader")
                    .file(new MockMultipartFile("file", "my_file.png", "image/png", inputStream))
            ).andExpect(
                    similarJsonObjectAs("classpath:/mock/webUploader.json")
            );
        }
    }

}