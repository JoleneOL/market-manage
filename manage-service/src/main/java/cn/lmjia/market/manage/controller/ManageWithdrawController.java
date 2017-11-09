package cn.lmjia.market.manage.controller;

import cn.lmjia.cash.transfer.CashTransferSupplier;
import cn.lmjia.cash.transfer.EntityOwner;
import cn.lmjia.cash.transfer.cjb.CjbSupplier;
import cn.lmjia.cash.transfer.model.CashTransferResult;
import cn.lmjia.cash.transfer.service.CashTransferService;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.Login_;
import cn.lmjia.market.core.entity.Manager;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest_;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.WithdrawService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

    private static final Log log = LogFactory.getLog(ManageWithdrawController.class);

    @Autowired
    private ConversionService conversionService;
    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private CashTransferService cashTransferService;
    @Autowired
    private ApplicationContext applicationContext;

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
    public void approval(@AuthenticationPrincipal Manager manager, long id, String comment, String transactionRecordNumber, EntityOwner owner, String bankName) throws Exception {
        CashTransferSupplier supplier = null;
        if ("兴业银行".equalsIgnoreCase(bankName)) {
            supplier = applicationContext.getBean(CjbSupplier.class);
        }
        //转账
        WithdrawRequest request = withdrawService.get(id);
        request.setComment(comment);
        request.setTransactionRecordNumber(transactionRecordNumber);
        try {
            CashTransferResult cashTransferResult = cashTransferService.cashTransfer(supplier, owner, request);
            withdrawService.approval(manager, id, cashTransferResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception();
        }
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
