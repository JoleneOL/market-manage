package cn.lmjia.market.core.entity.support;

/**
 * 标签类型
 * Created by helloztt on 2017-09-16.
 */
public enum TagType {
    SEARCH("商城分类"),
    LIST("首页列表显示");


    private final String message;

    TagType(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
