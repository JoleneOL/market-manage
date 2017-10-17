package cn.lmjia.cash.transfer.message.xyyh;

import java.util.Date;

/**
 *
 *包含交易流水（未指定起止时间，表示查余额；若指定起止时间，那么：
 *1）开始时间=结束时间；
 *2）开始时间早于结束时间，并且结束时间不为当天。。
 *建议查询指定某一天的流水，避免网络传输带来的超时）
 *
 * @author lxf
 */
public class Inctran {

    //开始时间 格式：YYYY-MM-DD（必输
    private Date dtStart;

    //结束时间 格式：YYYY-MM-DD（必输
    private Date dtEnd;

    //请求响应的页数（代表从第几页开始查询）（必输）
    private String page;

    //交易类型：0表示借方(往帐)  1表示贷方(来帐)默认查询借贷双方全部流水,非必输
    private String trnType;
}
