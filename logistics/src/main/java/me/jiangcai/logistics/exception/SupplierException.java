package me.jiangcai.logistics.exception;

/**
 * @author CJ
 */
public class SupplierException extends RuntimeException {
    public SupplierException(String message) {
        super(message);
    }

    public SupplierException(Throwable cause) {
        super(cause);
    }
}
