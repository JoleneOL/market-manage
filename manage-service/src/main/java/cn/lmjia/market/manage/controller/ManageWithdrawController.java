package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Login_;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest_;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.WithdrawService;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.field.FieldBuilder;
import me.jiangcai.crud.row.field.Fields;
import me.jiangcai.crud.row.supplier.JQueryDataTableDramatizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 管理提现,拥有root权限和财务权限的人可以管理.
 *
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_FINANCE + "')")
@Controller
public class ManageWithdrawController {

    @Autowired
    private ConversionService conversionService;
    @Autowired
    private WithdrawService withdrawService;

    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_FINANCE + "','" + Login.ROLE_LOOK + "')")
    @GetMapping("/withdrawManage")
    public String index() {
        return "_withdrawManage.html";
    }

    @PostMapping("/manage/withdraws/reject")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@AuthenticationPrincipal Manager manager, long id, String comment) {
        withdrawService.reject(manager, id, comment);
    }

    @PostMapping("/manage/withdraws/approval")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approval(@AuthenticationPrincipal Manager manager, long id, String comment, String transactionRecordNumber) {
        withdrawService.approval(manager, id, comment, transactionRecordNumber);
    }

    @PreAuthorize("hasAnyRole('ROOT','" + Login.ROLE_FINANCE + "','" + Login.ROLE_LOOK + "')")
    @GetMapping("/manage/withdraws")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition<WithdrawRequest> data(String name, String mobile
            , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-M-d") LocalDate orderDate
            , WithdrawStatus status) {
        WithdrawStatus realStatus = (status == null || status == WithdrawStatus.EMPTY) ? WithdrawStatus.checkPending : status;

        return new RowDefinition<WithdrawRequest>() {
            @Override
            public Class<WithdrawRequest> entityClass() {
                return WithdrawRequest.class;
            }

            @Override
            public List<FieldDefinition<WithdrawRequest>> fields() {
                return Arrays.asList(
                        Fields.asBasic("id")
                        , FieldBuilder.asName(WithdrawRequest.class, "loginId")
                                .addSelect(withdrawRequestRoot -> withdrawRequestRoot.get(WithdrawRequest_.whose).get(Login_.id))
                                .build()
                        , Fields.asBiFunction("user", ((root, criteriaBuilder)
                                -> ReadService.nameForLogin(root.join(WithdrawRequest_.whose)
                                , criteriaBuilder)))
                        , Fields.asBiFunction("userLevel", ((root, criteriaBuilder)
                                -> ReadService.agentLevelForLogin(root.join(WithdrawRequest_.whose)
                                , criteriaBuilder)))
                        , Fields.asBasic("comment")
                        , Fields.asBasic("transactionRecordNumber")
                        , Fields.asBasic("logisticsCode")
                        , Fields.asBasic("logisticsCompany")
                        , Fields.asBasic("actualAmount")
                        , Fields.asBasic("amount")
                        , Fields.asBasic("mobile")
                        , Fields.asBasic("account")
                        , Fields.asBasic("bank")
                        , Fields.asBasic("payee")
                        , FieldBuilder.asName(WithdrawRequest.class, "status")
                                .addSelect(root -> root.get(WithdrawRequest_.withdrawStatus))
                                .addFormat((data, type) -> data == null ? null : data.toString())
                                .build()
                        , FieldBuilder.asName(WithdrawRequest.class, "statusCode")
                                .addSelect(root -> root.get(WithdrawRequest_.withdrawStatus))
                                .addFormat((data, type) -> data == null ? null : ((Enum) data).ordinal())
                                .build()
                        , FieldBuilder.asName(WithdrawRequest.class, "requestTime")
                                .addFormat((data, type) -> conversionService.convert(data, String.class))
                                .build()
                );
            }

            @Override
            public Specification<WithdrawRequest> specification() {
                return (root, query, cb) -> {
                    Predicate predicate = cb.equal(root.get(WithdrawRequest_.withdrawStatus), realStatus);
                    if (!StringUtils.isEmpty(name)) {
                        predicate = cb.and(
                                predicate,
                                cb.or(cb.like(ReadService.nameForLogin(root.join(WithdrawRequest_.whose)
                                        , cb), "%" + name + "%")
                                        , cb.like(root.get(WithdrawRequest_.payee), "%" + name + "%")
                                )
                        );
                    }
                    if (!StringUtils.isEmpty(mobile)) {
                        predicate = cb.and(predicate
                                , cb.equal(root.get(WithdrawRequest_.mobile), "%" + mobile + "%")
                        );
                    }

                    if (orderDate != null) {
                        predicate = cb.and(predicate
                                , JpaFunctionUtils.dateEqual(cb, root.get(WithdrawRequest_.requestTime), orderDate)
                        );
                    }
                    return predicate;
                };
            }
        };
    }


}
