package cn.lmjia.market.core.service;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Order;
import cn.lmjia.market.core.entity.ProductType;
import cn.lmjia.market.core.entity.support.Address;
import me.jiangcai.wx.model.Gender;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CJ
 */
public interface OrderService {

    /**
     * 新创建订单
     *
     * @param who                创建者，也将是支付者
     * @param recommendBy        推荐人，必有的
     * @param name               客户
     * @param mobile             客户手机
     * @param age                年龄
     * @param gender             性别
     * @param installAddress     安装地址
     * @param product            产品
     * @param amount             数量
     * @param mortgageIdentifier 可选的按揭识别码
     * @return 新创建的订单
     */
    @Transactional
    Order newOrder(Login who, Login recommendBy, String name, String mobile, int age, Gender gender
            , Address installAddress
            , ProductType product
            , int amount, String mortgageIdentifier);

}
