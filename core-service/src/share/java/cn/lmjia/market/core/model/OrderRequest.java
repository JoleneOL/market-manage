package cn.lmjia.market.core.model;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import lombok.Data;
import me.jiangcai.jpa.entity.support.Address;

/**
 * @author CJ
 */
@Data
public class OrderRequest {
    private final Address address;
    private final MainGood good;
    private final String code;
    private final Login recommend;
    private final String name;
    private final int age;
    private final int gender;
    private final String mobile;
    private final int amount;
    private final String authorising;
    private final String idNumber;
    private final Long channelId;
}
