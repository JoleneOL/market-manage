package cn.lmjia.market.core.service;

import cn.lmjia.market.core.CoreServiceTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@Ignore
public class QRCodeServiceTest extends CoreServiceTest {

    @Autowired
    private QRCodeService qrCodeService;

    @Test
    public void generateQRCode() throws Exception {
        BufferedImage image = qrCodeService.generateQRCode("weixin://wxpay/bizpayurl?pr=THkIR3X");
        JOptionPane.showMessageDialog(null, new ImageIcon(image));
        assertThat(true)
                .isTrue();
    }

}