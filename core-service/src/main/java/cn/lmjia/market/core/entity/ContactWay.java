package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.entity.support.Address;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 联系方式
 *
 * @author CJ
 */
@Getter
@Setter
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "mobile")
})
public class ContactWay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20)
    private String mobile;
    /**
     * 联系人
     */
    @Column(length = 50)
    private String name;

    private Address address;
    @Column(length = 60)
    private String frontImagePath;
    @Column(length = 60)
    private String backImagePath;

    @Override
    public String toString() {
        if (StringUtils.isEmpty(name) && StringUtils.isEmpty(mobile))
            return "";
        if (StringUtils.isEmpty(name))
            return mobile;
        if (StringUtils.isEmpty(mobile))
            return name;
        return name + "(" + mobile + ")";
    }
}
