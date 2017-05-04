package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.entity.AgentLevel;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.selection.FieldDefinition;
import cn.lmjia.market.core.selection.JQueryDataTableDramatizer;
import cn.lmjia.market.core.selection.RowCustom;
import cn.lmjia.market.core.selection.RowDefinition;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.dealer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 可以调整代理的控制器
 *
 * @author CJ
 */
@Controller
@RequestMapping("/agentData")
public class AgentDataController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private ReadService readService;

    @GetMapping(value = "/list")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition data(@AuthenticationPrincipal Login login, String agentName) {
        return new RowDefinition<AgentLevel>() {

            @Override
            public Class<AgentLevel> entityClass() {
                return AgentLevel.class;
            }

            @Override
            public List<FieldDefinition> fields() {
                return Arrays.asList(
                        new AgentLevelField() {
                            @Override
                            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<?> root) {
                                return root;
                            }

                            @Override
                            protected Object export(AgentLevel level, Function<List, ?> exportMe) {
                                return level.getId();
                            }

                            @Override
                            public String name() {
                                return "id";
                            }

                            @Override
                            public Expression<?> order(Root<?> root, CriteriaBuilder criteriaBuilder) {
                                return root.get("id");
                            }

                        }, new AgentLevelField() {
                            @Override
                            protected Object export(AgentLevel level, Function<List, ?> exportMe) {
                                return level.getRank();
                            }

                            @Override
                            public String name() {
                                return "rank";
                            }

                            @Override
                            public Expression<?> order(Root<?> root, CriteriaBuilder criteriaBuilder) {
                                return root.get("rank");
                            }
                        }, new AgentLevelField() {
                            @Override
                            protected Object export(AgentLevel level, Function<List, ?> exportMe) {
                                return readService.nameForPrincipal(level.getLogin());
                            }

                            @Override
                            public String name() {
                                return "name";
                            }

                            @Override
                            public Expression<?> order(Root<?> root, CriteriaBuilder criteriaBuilder) {
                                return root.get("login");
                            }
                        }, new AgentLevelField() {
                            @Override
                            protected Object export(AgentLevel level, Function<List, ?> exportMe) {
                                return readService.mobileFor(level.getLogin());
                            }

                            @Override
                            public String name() {
                                return "phone";
                            }

                            @Override
                            public Expression<?> order(Root<?> root, CriteriaBuilder criteriaBuilder) {
                                return root.get("login");
                            }
                        }, new AgentLevelField() {
                            @Override
                            protected Object export(AgentLevel level, Function<List, ?> exportMe) {
                                return "???";
                            }

                            @Override
                            public String name() {
                                return "subordinate";
                            }

                            @Override
                            public Expression<?> order(Root<?> root, CriteriaBuilder criteriaBuilder) {
                                return null;
                            }
                        }, new AgentLevelField() {
                            @Override
                            protected Object export(AgentLevel level, Function<List, ?> exportMe) {
                                return exportMe.apply(level.getSubAgents());
                            }

                            @Override
                            public String name() {
                                return "children";
                            }

                            @Override
                            public Expression<?> order(Root<?> root, CriteriaBuilder criteriaBuilder) {
                                return null;
                            }
                        }
                );
            }

            @Override
            public Specification<AgentLevel> specification() {
                return agentService.manageable(login, agentName);
            }
        };
    }

    // 写一个最有可能实现 也是最具备描述性的方案
    private abstract class AgentLevelField implements FieldDefinition {
        @Override
        public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<?> root) {
            return null;
        }

        @Override
        public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
            return export((AgentLevel) origin, exportMe);
        }

        protected abstract Object export(AgentLevel level, Function<List, ?> exportMe);
    }

}
