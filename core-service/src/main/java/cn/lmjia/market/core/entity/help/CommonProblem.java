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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommonProblem that = (CommonProblem) o;

        if (enable != that.enable) return false;
        if (hot != that.hot) return false;
        if (weight != that.weight) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) return false;
        return updateTime != null ? updateTime.equals(that.updateTime) : that.updateTime == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (enable ? 1 : 0);
        result = 31 * result + (hot ? 1 : 0);
        result = 31 * result + weight;
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }

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
