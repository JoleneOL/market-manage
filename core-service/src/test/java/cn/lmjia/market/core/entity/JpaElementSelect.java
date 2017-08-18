package cn.lmjia.market.core.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author CJ
 */
@Entity
@Data
public class JpaElementSelect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Transient
//    @ElementCollection
    private List<OneForElement> elementList;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OneForEntity> entityList;

}
