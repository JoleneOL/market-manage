package cn.lmjia.market.dealer.entity;

import cn.lmjia.market.core.entity.Login;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Agent {

    @OneToOne
    private Login login;

}
