package cn.lmjia.market.core.entity.trj;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 按揭码信息
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class AuthorisingInfo {
    /**
     * 即按揭码
     */
    @Id
    @Column(length = 30)
    private String id;
    /**
     * 身份证
     */
    @Column(length = 18)
    private String idNumber;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime createdTime;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime usedTime;
    /**
     * 最后一次审核时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime auditingTime;
    private AuthorisingStatus authorisingStatus = AuthorisingStatus.Unused;
    /**
     * 交互留言
     */
    @Column(length = 100)
    private String message;
    /**
     * 结算时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime settlementTime;

    public boolean isUsed() {
        return authorisingStatus != AuthorisingStatus.Unused;
    }
}
