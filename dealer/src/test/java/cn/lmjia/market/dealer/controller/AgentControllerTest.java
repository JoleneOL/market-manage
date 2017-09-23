package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.ManageLevel;
import cn.lmjia.market.dealer.DealerServiceTest;
import me.jiangcai.jpa.entity.support.Address;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author CJ
 */
public class AgentControllerTest extends DealerServiceTest {

    //curl 'http://localhost:55555/market-manage/dealer/src/main/resources/dealer-view/addAgent.html' -H 'Cookie: Idea-390470b1=20f86e89-5fd1-46f7-b19e-172f714e3451; __utma=111872281.1550433883.1463023041.1468930698.1468956142.15; Idea-390470b3=a1f71123-38e9-4790-8993-1b4f55050f10; ckCsrfToken=Ydn9jsKu29bd5lMf1M9ScSwfNIqhOuYSbswlq7Cc' -H 'Origin: http://localhost:55555' -H 'Accept-Encoding: gzip, deflate, br' -H 'Accept-Language: zh-CN,zh;q=0.8' -H 'Upgrade-Insecure-Requests: 1' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36' -H 'Content-Type: application/x-www-form-urlencoded' -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8' -H 'Cache-Control: max-age=0' -H 'Referer: http://localhost:55555/market-manage/dealer/src/main/resources/dealer-view/addAgent.html' -H 'Connection: keep-alive'
    // --data 'rank=%E6%B5%99%E6%B1%9F%E7%9C%81%E4%BB%A3%E7%90%86&agentName=%E5%BE%90%E5%8C%97%E6%96%B9&firstPayment=30000&agencyFee=30000&beginDate=2017-5-1&endDate=2018-5-1&mobile=18829901010&password=123456&guideUser=1&address=%E6%B2%B3%E5%8C%97%E7%9C%81%2F%E7%A7%A6%E7%9A%87%E5%B2%9B%E5%B8%82%2F%E5%8C%97%E6%88%B4%E6%B2%B3%E5%8C%BA&fullAddress=111111&cardFrontPath=filePath&cardBackPath=filePath&cardFront=&cardBack=' --compressed
    //curl 'http://localhost:55555/market-manage/dealer/src/main/resources/dealer-view/addAgent.html' -H 'Cookie: Idea-390470b1=20f86e89-5fd1-46f7-b19e-172f714e3451; __utma=111872281.1550433883.1463023041.1468930698.1468956142.15; Idea-390470b3=a1f71123-38e9-4790-8993-1b4f55050f10; ckCsrfToken=Ydn9jsKu29bd5lMf1M9ScSwfNIqhOuYSbswlq7Cc' -H 'Origin: http://localhost:55555' -H 'Accept-Encoding: gzip, deflate, br' -H 'Accept-Language: zh-CN,zh;q=0.8' -H 'Upgrade-Insecure-Requests: 1' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36' -H 'Content-Type: application/x-www-form-urlencoded' -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8' -H 'Cache-Control: max-age=0' -H 'Referer: http://localhost:55555/market-manage/dealer/src/main/resources/dealer-view/addAgent.html' -H 'Connection: keep-alive'
    // --data 'superiorId=1&rank=%E6%B5%99%E6%B1%9F%E7%9C%81%E4%BB%A3%E7%90%86&agentName=%E5%BE%90%E5%8C%97%E6%96%B9&firstPayment=30000&agencyFee=30000&beginDate=2017-5-1&endDate=2018-5-1&mobile=18829901010&password=123456&guideUser=1&address=%E5%8C%97%E4%BA%AC%E5%B8%82%2F%E5%8C%97%E4%BA%AC%E5%B8%82%2F%E4%B8%9C%E5%9F%8E%E5%8C%BA&fullAddress=111111111&cardFrontPath=filePath&cardBackPath=filePath&cardFront=&cardBack=' --compressed
    @Test
    public void indexForAdd() throws Exception {
        // 此处分2种情况

        Manager manager = newRandomManager("", ManageLevel.customerManager);
        runWith(manager, () -> {
            mockMvc.perform(get("/addAgent"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                    .andExpect(view().name("addAgent.html"));

            String rank = "某代理商" + RandomStringUtils.randomAlphabetic(10);
            String agentName = "某人" + RandomStringUtils.randomAlphabetic(10);
            int firstPayment = 1000 + random.nextInt(10000);
            int agencyFee = 500 + random.nextInt(600);
            LocalDate beginDate = LocalDate.now().minusMonths(2);
            LocalDate endDate = LocalDate.now().plusMonths(2);
            String mobile = randomMobile();
            String password = UUID.randomUUID().toString();
            Login guideUser = randomLogin(false);
            Address address = randomAddress();
            String cardFrontPath = newRandomImagePath();
            String cardBackPath = newRandomImagePath();
            String businessLicensePath = newRandomImagePath();

            mockMvc.perform(post("/addAgent")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("rank", rank)
                    .param("agentName", agentName)
                    .param("firstPayment", String.valueOf(firstPayment))
                    .param("agencyFee", String.valueOf(agencyFee))
                    .param("beginDate", toText(beginDate))
                    .param("endDate", toText(endDate))
                    .param("mobile", mobile)
                    .param("password", password)
                    .param("guideUser", String.valueOf(guideUser.getId()))
                    .param("address", address.getStandardWithoutOther())
                    .param("fullAddress", address.getOtherAddress())
                    .param("cardFrontPath", cardFrontPath)
                    .param("cardBackPath", cardBackPath)
                    .param("businessLicensePath", businessLicensePath)
            )
                    .andDo(print())
                    .andExpect(status().isFound())
            ;

            // TODO 数据校验，以及因为手机号码的排他性，所以需要在页面上做远程校验 同时检查登录名以及联系表的手机号码
            return null;
        });

    }

}