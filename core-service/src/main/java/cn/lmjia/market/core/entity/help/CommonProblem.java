package cn.lmjia.market.core.entity.help;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 常见问题
 * @author lxf
 */
@Setter
@Getter
@Entity
public class CommonProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 标题
     */
    @Column(length = 50)
    private String title;

    /**
     * 问题解决办法, 富文本编辑.
     */
    @Lob
    private String content;

    /**
     * 是否启用
     */
    private boolean enable=true;

    /**
     * 是否在常见问题列表里展示.
     */
    private boolean hot;

    /**
     * 权重,排序.
     */
    private int weight = 50;

    /**
     * 创建时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 修改时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return "CommonProblem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", enable=" + enable +
                ", hot=" + hot +
                ", weight=" + weight +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
