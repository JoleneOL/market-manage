package cn.lmjia.market.core.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.jpa.entity.support.Address;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

/**
 * 联系方式
 *
 * @author CJ
 */
@Getter
@Setter
@Entity
//@Table(uniqueConstraints = {
//        @UniqueConstraint(columnNames = "mobile")
//})
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
    /**
     * 曾用名
     */
    @ElementCollection
    private List<String> usedNames;

    private Address address;
    @Column(length = 60)
    private String frontImagePath;
    @Column(length = 60)
    private String backImagePath;
    @Column(length = 68)
    private String businessLicensePath;

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
