package cn.lmjia.cash.transfer;

public class CashTransferServiceTest {

    /**
     * 现金转账测试
     * 目前只有一个主体->利每家,假定主体都是利每家的情况.
     * @param supplier 供应商
     * @param fBank 支付的银行
     * @param cashReceiver 提现申请的信息
     */
    private void cashTransferController(CashTransferSupplier supplier,String fBank,CashReceiver cashReceiver){
        String result = cashTransferService(supplier,fBank, cashReceiver);
        //将结果信息反馈给财务.
    }

    private String cashTransferService (CashTransferSupplier supplier,String fBank,CashReceiver cashReceiver){
        if(supplier == null){
            //默认就是利每家.
        }
        //根据业务主和付款银行获取付款人帐号信息,暂时以字符串表示.
        String fAccount = "付款人信息";
        //根据不同的银行调用不同银行的接口发送请求,假定兴业银行
        if("兴业银行".equals(fBank)){
            //调用兴业银行接口
            return cjbService(fAccount,cashReceiver);
        }
        return "FAILURE";

    }
    /**
     * 假设一个银行接口服务
     * @param fAccount 付款人信息
     * @param cashReceiver 收款人信息
     * @return
     */
    private String cjbService(String fAccount,CashReceiver cashReceiver){
        //设置是谁支付这笔用户提现.(,付款人帐号,"付款人姓名,开户行,汇款城市")引号内不是必须的.
        //设置参数
        //发送请求
        //接受响应参数,处理响应参数
        //返回响应结果(成功,失败),如果失败,返回失败原因.
        return "SUCCESS";
    }
}
