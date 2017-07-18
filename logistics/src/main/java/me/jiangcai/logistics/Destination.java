package me.jiangcai.logistics;

/**
 * 目的地
 *
 * @author CJ
 */
public interface Destination {
    /**
     * @return 收货人所在省
     */
    String getProvince();

    /**
     * @return 收货人所在市
     */
    String getCity();

    /**
     * @return 收货人所在县/区
     */
    String getCountry();

    /**
     * @return 收货人详细地址
     */
    String getDetailAddress();

    /**
     * @return 收货人姓名
     */
    String getConsigneeName();

    /**
     * @return 收货人手机
     */
    String getConsigneeMobile();

}
