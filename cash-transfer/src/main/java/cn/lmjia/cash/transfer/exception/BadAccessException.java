package cn.lmjia.cash.transfer.exception;

/**
 * 无法访问异常
 *
 * @author CJ
 */
public class BadAccessException extends Exception {
    public BadAccessException(){

    }
    public BadAccessException(String s){
        super(s);
    }
    public BadAccessException(Exception e){
        super(e);
    }
}
