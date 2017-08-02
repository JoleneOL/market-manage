package cn.lmjia.market.core.trj;

import cn.lmjia.market.core.CoreServiceTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author CJ
 */
public class TRJServiceTest extends CoreServiceTest {

    private static final Log log = LogFactory.getLog(TRJServiceTest.class);
    @Autowired
    private TRJService trjService;

    @Test
    public void sign() {
        String result = trjService.sign(new ArrayList<>(Arrays.asList(
                new BasicNameValuePair("p1", "1")
                , new BasicNameValuePair("p2", "2")
                , new BasicNameValuePair("p3", "hahaha")
        )));
        log.info(result);
    }

}