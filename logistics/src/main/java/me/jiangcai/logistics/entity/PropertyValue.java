package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 描述货品性质、规格的参数
 * Created by helloztt on 2017/9/12.
 */
@Setter
@Getter
@Entity
public class PropertyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 参数值
     */
    private String value;
    /**
     * 默认图标
     */
    private String icon;
    /**
     * 排序
     */
    private int order;
    /**
     * 是否有效
     */
    private boolean disabled;
}
