package cn.lmjia.market.core.trj;

/**
 * 按揭码无效
 *
 * @author CJ
 */
public class InvalidAuthorisingException extends Exception {

    private final String authorising;
    private final String idNumber;

    public InvalidAuthorisingException(String authorising, String idNumber) {
        this.authorising = authorising;
        this.idNumber = idNumber;
    }
}
