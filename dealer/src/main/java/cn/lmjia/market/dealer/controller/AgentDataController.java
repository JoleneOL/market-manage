package cn.lmjia.market.dealer.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.AgentLevel;
import cn.lmjia.market.core.entity.deal.AgentLevel_;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.dealer.service.AgentService;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.field.Fields;
import me.jiangcai.crud.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.crud.row.supplier.Select2Dramatizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.NumberUtils;
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

    @GetMapping(value = "/listRuling")
    @RowCustom(dramatizer = Select2Dramatizer.class, distinct = true)
    public RowDefinition listRuling(@AuthenticationPrincipal Login login, String search) {
        return new RowDefinition<AgentLevel>() {

            @Override
            public Class<AgentLevel> entityClass() {
                return AgentLevel.class;
            }

            @Override
            public List<FieldDefinition<AgentLevel>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , Fields.asBasic("level")
                        , new FieldDefinition<AgentLevel>() {
                            @Override
                            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<AgentLevel> root) {
                                return JpaFunctionUtils.contact(criteriaBuilder, root.get("rank")
                                        , criteriaBuilder.literal(" "), root.get("level"));
                            }

                            @Override
                            public String name() {
                                return "rank";
                            }

                            @Override
                            public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
                                String data = (String) origin;
                                int index = data.lastIndexOf(' ');
                                return data.substring(0, index) + "("
                                        + agentService.getLoginTitle(NumberUtils.parseNumber(data.substring(index + 1)
                                        , Integer.class)) + ")";
                            }

                            @Override
                            public Expression<?> order(Root<AgentLevel> root, CriteriaBuilder criteriaBuilder) {
                                return root.get("rank");
                            }
                        }
                );
            }

            @Override
            public Specification<AgentLevel> specification() {
                return agentService.manageableAndRuling(false, login, search);
            }
        };
    }

    @GetMapping(value = "/list")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition data(@AuthenticationPrincipal Login login, String agentName) {
        return new RowDefinition<AgentLevel>() {

            @Override
            public Class<AgentLevel> entityClass() {
                return AgentLevel.class;
            }

            @Override
            public List<FieldDefinition<AgentLevel>> fields() {
                return Arrays.asList(
                        new AgentLevelField() {
                            @Override
                            public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<AgentLevel> root) {
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
                            public Expression<?> order(Root<AgentLevel> root, CriteriaBuilder criteriaBuilder) {
                                return root.get("id");
                            }

                        }, new AgentLevelField() {
                            @Override
                            protected Object export(AgentLevel level, Function<List, ?> exportMe) {
                                return level.getRank() + "(" + agentService.loginTitle(level) + ")";
                            }

                            @Override
                            public String name() {
                                return "rank";
                            }

                            @Override
                            public Expression<?> order(Root<AgentLevel> root, CriteriaBuilder criteriaBuilder) {
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
                            public Expression<?> order(Root<AgentLevel> root, CriteriaBuilder criteriaBuilder) {
                                return root.get(AgentLevel_.login);
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
                            public Expression<?> order(Root<AgentLevel> root, CriteriaBuilder criteriaBuilder) {
                                return root.get(AgentLevel_.login);
                            }
                        }, new AgentLevelField() {
                            @Override
                            protected Object export(AgentLevel level, Function<List, ?> exportMe) {
                                return "";
                            }

                            @Override
                            public String name() {
                                return "subordinate";
                            }

                            @Override
                            public Expression<?> order(Root<AgentLevel> root, CriteriaBuilder criteriaBuilder) {
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
                            public Expression<?> order(Root<AgentLevel> root, CriteriaBuilder criteriaBuilder) {
                                return null;
                            }
                        }
                );
            }

            @Override
            public Specification<AgentLevel> specification() {
                return agentService.manageable(false, login, agentName);
            }
        };
    }

    // 写一个最有可能实现 也是最具备描述性的方案
    private abstract class AgentLevelField implements FieldDefinition<AgentLevel> {
        @Override
        public Selection<?> select(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, Root<AgentLevel> root) {
            return null;
        }

        @Override
        public Object export(Object origin, MediaType mediaType, Function<List, ?> exportMe) {
            return export((AgentLevel) origin, exportMe);
        }

        protected abstract Object export(AgentLevel level, Function<List, ?> exportMe);
    }

}
