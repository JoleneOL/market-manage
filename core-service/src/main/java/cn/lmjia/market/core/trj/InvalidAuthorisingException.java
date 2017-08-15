package cn.lmjia.market.core.trj;

import lombok.Getter;

/**
 * 按揭码无效
 *
 * @author CJ
 */
@Getter
public class InvalidAuthorisingException extends Exception {

    private final String authorising;
    private final String idNumber;

    public InvalidAuthorisingException(String authorising, String idNumber) {
        this.authorising = authorising;
        this.idNumber = idNumber;
    }
}
