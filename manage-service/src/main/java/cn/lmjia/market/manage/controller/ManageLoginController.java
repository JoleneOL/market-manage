package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Login_;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.service.ReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 管理身份
 *
 * @author CJ
 */
@Controller
public class ManageLoginController {

    @Autowired
    private ConversionService conversionService;

    @GetMapping("/loginManage")
    public String index() {
        return "_loginManage.html";
    }

    @GetMapping("/manage/login")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition list(String name) {
        return new RowDefinition<Login>() {
            @Override
            public Class<Login> entityClass() {
                return Login.class;
            }

            @Override
            public List<FieldDefinition<Login>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(Login.class, "name")
                                .addBiSelect(ReadService::nameForLogin)
                                .build()
                        , FieldBuilder.asName(Login.class, "mobile")
                                .addBiSelect(ReadService::mobileForLogin)
                                .build()
                        , FieldBuilder.asName(Login.class, "createdTime")
                                .addFormat((data, type) -> conversionService.convert(data, String.class))
                                .build()
                        , FieldBuilder.asName(Login.class, "level")
                                .addBiSelect(ReadService::agentLevelForLogin)
                                .build()
                        , FieldBuilder.asName(Login.class, "wechatID")
                                .addSelect(loginRoot -> loginRoot.join(Login_.wechatUser, JoinType.LEFT).get("openId"))
                                .build()
                        , FieldBuilder.asName(Login.class, "state")
                                .addSelect(loginRoot -> loginRoot.get(Login_.enabled))
                                .addFormat(toBi(state -> state ? "启用" : "禁用"))
                                .build()
                        , FieldBuilder.asName(Login.class, "stateCode")
                                .addSelect(loginRoot -> loginRoot.get(Login_.enabled))
                                .addFormat(toBi(state -> state ? 0 : 1))
                                .build()
                );
            }

            @Override
            public Specification<Login> specification() {
                // root 是不可见，也不可以编辑的
                return (root, query, cb) -> {
                    Predicate predicate = cb.and(cb.notEqual(root.get(Login_.loginName), "root")
                            , cb.notEqual(root.type(), Manager.class)
                    );
                    if (!StringUtils.isEmpty(name)) {
                        predicate = cb.and(
                                predicate
                                , cb.like(root.get(Login_.loginName), "%" + name + "%")
                        );
                    }
                    return predicate;
                };
            }
        };
    }

    private BiFunction<Object, MediaType, Object> toBi(Function<Boolean, Object> function) {
        return (o, mediaType) -> {
            if (o == null)
                return null;
            Boolean manager = (Boolean) o;
            return function.apply(manager);
        };
    }

}
