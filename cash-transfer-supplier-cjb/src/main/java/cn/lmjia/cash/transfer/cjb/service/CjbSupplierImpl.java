package cn.lmjia.cash.transfer.cjb.service;

import cn.lmjia.cash.transfer.*;
import cn.lmjia.cash.transfer.cjb.CjbSupplier;
import cn.lmjia.cash.transfer.cjb.message.*;
import cn.lmjia.cash.transfer.cjb.message.sonrq.SignonMsgsRQV1;
import cn.lmjia.cash.transfer.cjb.message.sonrq.Sonrq;
import cn.lmjia.cash.transfer.cjb.message.transfer.*;
import cn.lmjia.cash.transfer.cjb.message.transfer.query.*;
import cn.lmjia.cash.transfer.exception.BadAccessException;
import cn.lmjia.cash.transfer.exception.SupplierApiUpgradeException;
import cn.lmjia.cash.transfer.exception.TransferFailureException;
import cn.lmjia.cash.transfer.model.CashTransferResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author lxf
 */
@Component("CjbSupplier")
public class CjbSupplierImpl implements CjbSupplier {
    private static final Log log = LogFactory.getLog(CjbSupplierImpl.class);

    private final String sendUrl;

    public CjbSupplierImpl(Environment environment) {
        this.sendUrl = environment.getProperty("cjb.URL", "http://120.55.60.148:8008");
    }

    @Override
    public Map<String, Object> cashTransfer(OwnerAccount account, CashReceiver receiver) throws BadAccessException, TransferFailureException, JsonProcessingException {
        //报文根标签对象.
        Fox requestFox = new Fox();
        //将登录信息转换成报文对象.
        SignonMsgsRQV1 signonMsgsRQV1 = usSonrq(account);
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

        //结果
        return cashTransferResult(responseFox);
    }

    @Override
    public Map<String, Object> statusQuery(OwnerAccount account, CashReceiver receuver) throws BadAccessException, JsonProcessingException, TransferFailureException {
        //报文根标签对象.
        Fox requestFox = new Fox();
        Securities_msgsRQV1 securities_msgsRQV1 = new Securities_msgsRQV1();
        //将登录信息转换成报文对象.
        SignonMsgsRQV1 signonMsgsRQV1 = usSonrq(account);
        requestFox.setSignonMsgsRQV1(signonMsgsRQV1);
        //封装查询信息根对象
        XferInqTrnRq xferInqTrnRq = new XferInqTrnRq();
        //该次查询的TrunId
        xferInqTrnRq.setTrnuId(LocalDateTime.now().format(fotmatterYear) + UUID.randomUUID().toString().replace("-", ""));

        XferInqRq xferInqRq = new XferInqRq();
        //要查询的转账交易TrunId
        xferInqRq.setClientRef(receuver.getWithdrawId().toString());

        xferInqTrnRq.setXferInqRq(xferInqRq);
        securities_msgsRQV1.setXferInqTrnRq(xferInqTrnRq);
        requestFox.setSecurities_msgsRQV1(securities_msgsRQV1);
        //发送请求
        Fox responseFox = sendRequest(requestFox);
        return cashTransferResult(responseFox);
    }


    /**
     * 处理现金转账,查询转账状态响应结果处理
     *
     * @param responseFox 响应的报文对象
     * @return 结果信息
     * @throws BadAccessException       业务主登录出错时
     * @throws TransferFailureException 转账失败时
     */
    private CashTransferResult cashTransferResult(Fox responseFox) throws BadAccessException, TransferFailureException {
        //看登录信息是否成功
        Status status = responseFox.getSignonMsgsRSV1().getSonrs().getStatus();
        if (!"0".equals(status.getCode())) {
            //登录失败
            throw new BadAccessException("错误码:" + status.getCode() + "登录信息错误:" + status.getMessage() + " 处理时间" + responseFox.getSignonMsgsRSV1().getSonrs().getDtServer());
        }
        //现金转账响应
        XferTrnRs xferTrnRs = responseFox.getSecurities_msgsRSV1().getXferTrnRs();
        //查询现金转账响应
        XferInqTrnRs xferInqTrnRs = responseFox.getSecurities_msgsRSV1().getXferInqTrnRs();
        Map<String, Object> result = new HashMap<>();

        CashTransferResult cashTransferResult = new CashTransferResult();
        if (xferTrnRs != null) {
            //转账请求
            Status transferStatus = xferTrnRs.getStatus();
            if (!"0".equals(transferStatus.getCode())) {
                //失败的请求
                throw new TransferFailureException("转账错误码:" + transferStatus.getCode() + "转账错误信息:" + transferStatus.getMessage() + "错误的提现申请单号:" + xferTrnRs.getTrnuId());
            }
            //成功
            XferRs xferRs = xferTrnRs.getXferRs();
            result.put("TRNUID", xferTrnRs.getTrnuId());
            result.put("SRVRID", xferRs.getSrvrId());
            result.put("MEMO", xferRs.getXferInfo().getMemo());
            result.put("XFERPRCCODE", xferRs.getXferPrcsts().getXferPrcCode());
            //指令处理时间 yyyy-MM-dd HH:mm:ss
            result.put("DTXFERPRC", LocalDateTime.parse(xferRs.getXferPrcsts().getDtXferPrc(), formatter));
            String message = xferRs.getXferPrcsts().getMessage();
            if (StringUtils.isNotBlank(message)) {
                result.put("MESSAGE", message);
            }
        } else if (xferInqTrnRs != null) {
            //查询转账状态请求
            Status transferStatus = xferInqTrnRs.getStatus();
            if (!"0".equals(transferStatus.getCode())) {
                //失败的请求
                throw new TransferFailureException("查询失败错误码:" + transferStatus.getCode() + "查询失败错误信息:" + transferStatus.getMessage() );
            }
            //成功
            cashTransferResult.setClientSerial(xferInqTrnRs.getTrnuId());
            XferList xferList = xferInqTrnRs.getXferInqRs().getXferList();
            String more = xferList.getMore();
            if("N".equalsIgnoreCase(more)){
                //没有记录
                throw new TransferFailureException("没有查询到转账记录");
            }else{
                //查询的转账记录 xfer是这条记录的信息.
                Xfer xfer = xferList.getXfer();
                cashTransferResult.setServiceSerial(xfer.getSrvrtId());
                cashTransferResult.setMemo(xfer.getXferInfo().getMemo());
                cashTransferResult.setResultStatuCode(xfer.getXferPrcsts().getXferPrcCode());
                //指令处理时间 yyyy-MM-dd HH:mm:ss
                cashTransferResult.setProcessingTime(LocalDateTime.parse(xfer.getXferPrcsts().getDtXferPrc(), formatter));
                String message = xfer.getXferPrcsts().getMessage();
                if (StringUtils.isNotBlank(message)) {
                    cashTransferResult.setMessage(message);
                }
            }
        }
        return cashTransferResult;
    }

    @Override
    public String getName() {
        return "兴业银行";
    }

    @Override
    public BigDecimal queryBalance(EntityOwner owner) throws IOException, SupplierApiUpgradeException, BadAccessException {
        return null;
    }


    public Fox sendRequest(Fox requestMessage) throws JsonProcessingException, BadAccessException {
        return sendRequest(requestMessage, null, null);
    }

    public Fox sendRequest(Fox requestMessage, Boolean interBank, Boolean local) throws JsonProcessingException, BadAccessException {
        StringBuffer content = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\"?>");
        String xml = xmlMapper.writeValueAsString(requestMessage).replaceAll(" xmlns=\"\"", "");
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

    /**
     * @param account 账户信息
     * @return 登录指令对象
     */
    private SignonMsgsRQV1 usSonrq(OwnerAccount account) {
        Map<String, String> message = account.getLoginInformation();
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
        xferTrnRq.setTrnuId(receiver.getWithdrawId().toString());

        XferRq xferRq = new XferRq();
        XferInfo xferInfo = new XferInfo();
        //付款人账户信息
        Acctfrom acctfrom = new Acctfrom();
        acctfrom.setAcctId(account.getAccountNum());
        acctfrom.setBankDesc(account.getBankDesc());
        acctfrom.setCity(account.getCity());
        acctfrom.setName(account.getName());
        //收款人信息
        AcctTo acctTo = new AcctTo();
        acctTo.setAcctId(receiver.getAccountNum());
        acctTo.setBankNum(receiver.getBankNumber());
        acctTo.setName(receiver.getName());
        acctTo.setBankDesc(receiver.getBankDesc());
        //付款人银行和收款人银行是否是同个银行
        if (!account.getCity().equals(receiver.getCity())) {
            acctTo.setCity(receiver.getCity());
        }
        //转账金额
        xferInfo.setTrnAmt(receiver.getWithdrawAmount());
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
