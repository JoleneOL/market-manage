package cn.lmjia.market.core.entity.help;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
    private Long Id;

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
    private boolean enable;

    /**
     * 权重,是否在常见问题列表里展示.
     */
    private boolean isWeight;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommonProblem that = (CommonProblem) o;

        if (enable != that.enable) return false;
        if (isWeight != that.isWeight) return false;
        if (Id != null ? !Id.equals(that.Id) : that.Id != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        int result = Id != null ? Id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (enable ? 1 : 0);
        result = 31 * result + (isWeight ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CommonProblem{" +
                "Id=" + Id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", enable=" + enable +
                ", isWeight=" + isWeight +
                '}';
    }
}
