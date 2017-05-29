package cn.lmjia.market.dealer.controller.team;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.util.ApiDramatizer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author CJ
 */
@Controller
public class TeamDataController {

    // all
    // 总 1
    // 分 2
    // 经销 3
    // 客户 4
    @RowCustom(dramatizer = ApiDramatizer.class, distinct = true)
    @GetMapping("/api/teamList")
    public RowDefinition<AgentLevel> teamList(@AuthenticationPrincipal Login login, String rank) {
        // 实际上查的应该是 Login
        // login,info
        // select
        // l?.login,customer.login
        // from AgentLevel my
        // join l2 my.subAgents
        // ..
        // join customer
        //
        return new RowDefinition<AgentLevel>() {
            @Override
            public Class<AgentLevel> entityClass() {
                return AgentLevel.class;
            }

            @Override
            public List<FieldDefinition<AgentLevel>> fields() {
                return null;
            }

            @Override
            public Specification<AgentLevel> specification() {
                return null;
            }
        };
    }

}
