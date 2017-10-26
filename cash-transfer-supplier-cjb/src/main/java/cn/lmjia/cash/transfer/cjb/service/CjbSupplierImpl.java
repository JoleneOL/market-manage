package cn.lmjia.cash.transfer.cjb.service;

import cn.lmjia.cash.transfer.*;
import cn.lmjia.cash.transfer.cjb.CjbSupplier;
import cn.lmjia.cash.transfer.cjb.message.*;
import cn.lmjia.cash.transfer.cjb.message.sonrq.SignonMsgsRQV1;
import cn.lmjia.cash.transfer.cjb.message.sonrq.Sonrq;
import cn.lmjia.cash.transfer.cjb.message.transfer.*;
import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.SupplierApiUpgradeException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;
import com.fasterxml.jackson.core.JsonProcessingException;
import me.jiangcai.logistics.exception.SupplierException;
import me.jiangcai.logistics.haier.http.ResponseHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author lxf
 */
@Component
public class CjbSupplierImpl implements CjbSupplier {
    private static final Log log = LogFactory.getLog(CjbSupplierImpl.class);

    private String sendUrl;

    @Override
    public LocalDateTime cashTransfer(OwnerAccount account, CashReceiver receiver) throws BadAccessException, TransferFailureException, JsonProcessingException {
        //主体访问供应商的登录信息.
        Map<String, String> message = account.getSonrq();
        //报文根标签对象.
        Fox requestFox = new Fox();
        //将登录信息转换成报文对象.
        SignonMsgsRQV1 signonMsgsRQV1 = usSonrq(message);
        requestFox.setSignonMsgsRQV1(signonMsgsRQV1);
        //转账信息报文
        Securities_msgsRQV1 securities_msgsRQV1 = usSecurities(account, receiver);
        requestFox.setSecurities_msgsRQV1(securities_msgsRQV1);

        boolean interBank = receiver.getBankDesc().contains("兴业") ? true : false;
        boolean local = receiver.getCity().contains(account.getCity()) ? true : false;
        //发送消息
        Fox responseFox = sendRequest(requestFox, interBank, local);
        Status status = responseFox.getSecurities_msgsRSV1().getXferTrnRs().getStatus();
        log.info("转账请求处理结果响应码:" + status.getCode() + "请求处理结果响应信息:" + status.getMessage());

        //结果处理
        Map<String, Object> result = cashTransferResult(responseFox);
        return (LocalDateTime) result.get("DTXFERPRC");
    }

    /**
     * 处理
     *
     * @param responseFox 响应的报文对象
     * @return 结果信息
     * @throws BadAccessException       业务主登录出错时
     * @throws TransferFailureException 转账失败时
     */
    private Map<String, Object> cashTransferResult(Fox responseFox) throws BadAccessException, TransferFailureException {
        //看登录信息是否成功
        Status status = responseFox.getSignonMsgsRSV1().getSonrs().getStatus();
        if (!"0".equals(status.getCode())) {
            //登录失败
            throw new BadAccessException("错误码:" + status.getCode() + "登录信息错误:" + status.getMessage() + " 处理时间" + responseFox.getSignonMsgsRSV1().getSonrs().getDtServer());
        }
        XferTrnRs xferTrnRs = responseFox.getSecurities_msgsRSV1().getXferTrnRs();
        Status transferStatus = xferTrnRs.getStatus();
        if (!"0".equals(transferStatus.getCode())) {
            //失败的请求
            throw new TransferFailureException("转账错误码:" + transferStatus.getCode() + "转账错误信息:" + transferStatus.getMessage() + "错误的提现申请单号:" + xferTrnRs.getTrnuid());
        }
        //成功
        XferRs xferRs = xferTrnRs.getXferRs();
        Map<String, Object> result = new HashMap<>();
        result.put("TRNUID", xferTrnRs.getTrnuid());
        result.put("SRVRID", xferRs.getSrvrId());
        result.put("MEMO", xferRs.getXferInfo().getMemo());
        result.put("XFERPRCCODE", xferRs.getXferPrcsts().getXferPrcCode());
        //指令处理时间 yyyy-MM-dd HH:mm:ss
        result.put("DTXFERPRC", LocalDateTime.parse(xferRs.getXferPrcsts().getDtXferPrc(), formatter));
        String message = xferRs.getXferPrcsts().getMessage();
        if (StringUtils.isNotBlank(message)) {
            result.put("MESSAGE", message);
        }
        return result;
    }

    @Override
    public BigDecimal queryBalance(EntityOwner owner) throws IOException, SupplierApiUpgradeException, BadAccessException {
        return null;
    }


    private Fox sendRequest(Fox requestMessage) throws JsonProcessingException, BadAccessException {
        return sendRequest(requestMessage, null, null);
    }

    /**
     * 发送请求
     *
     * @param requestMessage 请求报文
     * @return 响应报文
     */
    private Fox sendRequest(Fox requestMessage, Boolean interBank, Boolean local) throws JsonProcessingException, BadAccessException {
        StringBuffer content = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\"?>");
        String xml = xmlMapper.writeValueAsString(requestMessage).replaceAll(" xmlns=\"\"","");
        content.append(xml);
        if (content.toString().contains("<ACCTTO")) {
            if (interBank != null || local != null) {
                if (interBank && local) {
                    content.insert(content.indexOf("<ACCTTO") + "<ACCTTO".length(), " INTERBANK='Y' LOCAL='Y'");
                } else if (interBank && !local) {
                    content.insert(content.indexOf("<ACCTTO") + "<ACCTTO".length(), " INTERBANK='Y' LOCAL='N'");
                } else if (!interBank && local) {
                    content.insert(content.indexOf("<ACCTTO") + "<ACCTTO".length(), " INTERBANK='N' LOCAL='Y'");
                } else {
                    content.insert(content.indexOf("<ACCTTO") + "<ACCTTO".length(), " INTERBANK='N' LOCAL='N'");
                }
            }
        }
        log.debug("发送的报文:" + content.toString());

        ByteArrayEntity requestEntity = null;
        try (CloseableHttpClient client = newClient()) {
            requestEntity = new ByteArrayEntity(content.toString().getBytes("GBK"));
            HttpPost post = new HttpPost(sendUrl);
            post.setEntity(requestEntity);
            post.addHeader("Content-Type", "application/x-fox");

            CloseableHttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            log.info("响应状态码:" + statusLine.getStatusCode());
            InputStreamReader inputStreamReader = new InputStreamReader(response.getEntity().getContent(), "utf-8");
            //返回响应报文.
            return xmlMapper.readValue(inputStreamReader, Fox.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadAccessException(e);
        }
    }

    private CloseableHttpClient newClient() {
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setSocketTimeout(30000)
                        .setConnectionRequestTimeout(30000)
                        .setConnectTimeout(30000)
                        .build())
                .build();
    }

    private SignonMsgsRQV1 usSonrq(Map<String, String> message) {
        SignonMsgsRQV1 signonMsgsRQV1 = new SignonMsgsRQV1();
        Sonrq sonrq = new Sonrq();
        sonrq.setCid(message.get("cid"));
        sonrq.setUserId(message.get("userId"));
        sonrq.setUserPass(message.get("userPass"));
        //不需要服务器产生userkey
        sonrq.setGenUserKey("N");
        //YYYY-MM-DD_HH:MM:SS
        String format = LocalDateTime.now().format(formatter);
        sonrq.setDtClient(format);
        signonMsgsRQV1.setSonrq(sonrq);
        return signonMsgsRQV1;
    }

    /**
     * @param account  付款人信息
     * @param receiver 收款人信息
     * @return 转账指令对象
     */
    private Securities_msgsRQV1 usSecurities(OwnerAccount account, CashReceiver receiver) {
        Securities_msgsRQV1 securities_msgsRQV1 = new Securities_msgsRQV1();
        XferTrnRq xferTrnRq = new XferTrnRq();
        //客户端交易的唯一标志，否则客户端将无法分辨响应报文的对应关系,最大30位建议值为YYYYMMDD+序号
        xferTrnRq.setTrnuId(LocalDateTime.now().format(fotmatterYear) + receiver.getId());

        XferRq xferRq = new XferRq();
        XferInfo xferInfo = new XferInfo();
        //付款人账户信息
        Acctfrom acctfrom = new Acctfrom();
        acctfrom.setAcctId(account.getAcctId());
        acctfrom.setBankDesc(account.getBankDesc());
        acctfrom.setCity(account.getCity());
        acctfrom.setName(account.getName());
        //收款人信息
        AcctTo acctTo = new AcctTo();
        acctTo.setAcctId(receiver.getAcctId());
        acctTo.setBankNum(receiver.getBankNumber());
        acctTo.setName(receiver.getName());
        acctTo.setBankDesc(receiver.getBankDesc());
        //付款人银行和收款人银行是否是同个银行
        if (!account.getCity().equals(receiver.getCity())) {
            acctTo.setCity(receiver.getCity());
        }
        //转账金额
        xferInfo.setTrnAmt(receiver.getAmount());
        //用款用途
        xferInfo.setPurPose(receiver.getPurpose());
        if (StringUtils.isNotBlank(receiver.getMemo())) {
            xferInfo.setMemo(receiver.getMemo());
        }
        xferRq.setXferInfo(xferInfo);
        xferTrnRq.setXferRq(xferRq);
        securities_msgsRQV1.setXferTrnRq(xferTrnRq);

        return securities_msgsRQV1;
    }


}
