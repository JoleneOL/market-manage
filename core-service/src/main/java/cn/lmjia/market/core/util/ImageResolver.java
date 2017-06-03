package cn.lmjia.market.core.util;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

/**
 * @author CJ
 */
public class ImageResolver implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType() == BufferedImage.class;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest) throws Exception {
        BufferedImage image = (BufferedImage) returnValue;

        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        response.setContentType("image/png");

        try (OutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            outputStream.flush();
        }

        mavContainer.setRequestHandled(true);
    }
}
