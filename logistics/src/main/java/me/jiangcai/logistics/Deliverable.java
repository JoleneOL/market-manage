package me.jiangcai.logistics;

import me.jiangcai.logistics.entity.support.DeliverableData;

/**
 * @author CJ
 */
public interface Deliverable {
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

    default DeliverableData toDeliverableData() {
        DeliverableData data = new DeliverableData();
        data.setProvince(getProvince());
        data.setPrefecture(getCity());
        data.setCounty(getCountry());
        data.setOtherAddress(getDetailAddress());
        data.setPeople(getConsigneeName());
        data.setMobile(getConsigneeMobile());
        return data;
    }
}
