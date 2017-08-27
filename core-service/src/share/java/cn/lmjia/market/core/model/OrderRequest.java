package cn.lmjia.market.core.model;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainGood;
import lombok.Data;
import me.jiangcai.jpa.entity.support.Address;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

/**
 * @author CJ
 */
@Data
public class OrderRequest {
    private final Address address;
    //    private final MainGood good;
    private final String code;
    private final Login recommend;
    private final String name;
    private final int age;
    private final int gender;
    private final String mobile;
    //    private final int amount;
    private final String authorising;
    private final String idNumber;
    private final Long channelId;
    private final Map<MainGood, Integer> goods;
    private boolean installmentHuabai;

    public MockHttpServletRequestBuilder forGoods(MockHttpServletRequestBuilder builder) {
        MockHttpServletRequestBuilder instance = builder;
        if(goods.size() == 0){
            for (MainGood good : goods.keySet()) {
                instance = instance.param("goods", good.getId() + "," + goods.get(good));
            }
        }
        for (MainGood good : goods.keySet()) {
            instance = instance.param("goods[]", good.getId() + "," + goods.get(good));
        }
        return instance;
    }
}
