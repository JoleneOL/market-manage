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
    @Column(length = 20)
    private String id;
    /**
     * 身份证
     */
    @Column(length = 18)
    private String idNumber;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime createdTime;
    private boolean used;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime usedTime;

}
