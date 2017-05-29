package cn.lmjia.market.core.entity;

import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.support.Address;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.wx.model.Gender;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import java.util.ArrayList;

/**
 * 客户
 * 如果电话和姓名都一样才可以被认为是同一个客户
 * 但是只要电话一致即可认为是同一个身份
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Customer {
    /**
     * 客户的等级
     */
    public static final int LEVEL = 100;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Login login;
    @Column(length = 20)
    private String mobile;
    @Column(length = 50)
    private String name;
    /**
     * 所属经销商 ,必然处于代理商链的最低端
     */
    @ManyToOne(optional = false, cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private AgentLevel agentLevel;


    private int birthYear;
    private Gender gender;
    private Address installAddress;

    public static Expression<String> getMobile(Path<Customer> path) {
        return path.get("mobile");
    }

    /**
     * 让这个客户从属于这个经销商
     *
     * @param agentLevel 最低端的经销商
     */
    public void setupAgentLevel(AgentLevel agentLevel) {
        setAgentLevel(agentLevel);
        if (agentLevel.getCustomers() == null)
            agentLevel.setCustomers(new ArrayList<>());
        agentLevel.getCustomers().add(this);
    }
}
