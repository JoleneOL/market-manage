package me.jiangcai.logistics.demo;

import me.jiangcai.logistics.demo.entity.DemoOrder;
import me.jiangcai.logistics.entity.Product;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author CJ
 */
public interface DemoProject {

    @Transactional
    DemoOrder createOrder(Map<Product, Integer> amounts);

}
