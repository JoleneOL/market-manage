package cn.lmjia.cash.transfer.exception;

/**
 * 转账失败时的异常.
 */
public class TransferFailureException extends Exception{
    public TransferFailureException(){

    }
    public TransferFailureException(String s){
        super(s);
    }
}
