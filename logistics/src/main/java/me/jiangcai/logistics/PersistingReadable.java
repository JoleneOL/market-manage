package me.jiangcai.logistics;

import java.io.Serializable;

/**
 * 可持久，并且可读的
 * 可持久的意思是它可以在JPA系统中持久，基于简单目的的考虑直接使用{@link Serializable Java标准序列化}，但是强烈推荐实现自定义的序列化
 *
 * @author CJ
 */
public interface PersistingReadable extends Serializable {
    /**
     * @return 以HTML方式渲染
     */
    String toHTML();
}
