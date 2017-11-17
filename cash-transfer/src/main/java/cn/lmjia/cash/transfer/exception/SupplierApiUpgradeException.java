package cn.lmjia.cash.transfer.exception;

/**
 * 相关API已更新而本地尚未支持
 *
 * @author CJ
 */
public class SupplierApiUpgradeException extends Exception {
    public SupplierApiUpgradeException(){

    }
    public SupplierApiUpgradeException(String s){
        super(s);
    }
    public SupplierApiUpgradeException(Exception e){
        super(e);
    }
}
