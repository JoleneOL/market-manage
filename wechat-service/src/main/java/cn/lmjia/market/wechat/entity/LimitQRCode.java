package cn.lmjia.market.wechat.entity;

import cn.lmjia.market.core.entity.Login;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

/**
 * 永久场景二维码
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class LimitQRCode {
    /**
     * 场景二维码的id
     */
    @Id
    private Integer id;
    /**
     * 创建者
     */
    @OneToOne
    private Login login;
    /**
     * 二维码图片地址
     */
    @Column(length = 170)
    private String imageUrl;
    /**
     * 实际上的地址，如果有定制化二维码样式的需求可以考虑
     */
    @Column(length = 50)
    private String url;
    /**
     * 最后使用的时间；太长时间没用 会被收回的
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime lastUseTime;
}
