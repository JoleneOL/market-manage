package cn.lmjia.market.core.service;

import com.google.zxing.WriterException;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 二维码服务
 * 这个二维码跟微信带有场景标识的公众号二维码并不相同。仅仅是简单的隐藏URL
 *
 * @author CJ
 */
public interface QRCodeService {

    /**
     * 生成二维码
     *
     * @param url 链接
     * @return 二维码图片
     * @throws IOException     一般不会
     * @throws WriterException bad url
     */
    BufferedImage generateQRCode(String url) throws IOException, WriterException;

    /**
     * 解读二维码
     *
     * @param image 图片
     * @return 链接地址
     * @throws IOException
     * @throws IllegalArgumentException 找不到二维码
     */
    String scanImage(BufferedImage image) throws IOException, IllegalArgumentException;

}
