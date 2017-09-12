package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 商品标签，商品与标签为多对多的关系，只可新增和删除，不可添加
 * Created by helloztt on 2017/9/12.
 */
@Entity
@Setter
@Getter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 标签名称
     */
    private String name;
    /**
     * 是否有子类目
     */
    private boolean isParent;
    /**
     * 上级类目
     */
    private Tag parentTag;
    /**
     * 标签图标
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
