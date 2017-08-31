$(function () {
    "use strict";

    $('#J_datePicker').flatpickr({
        maxDate: new Date(),
        locale: 'zh'
    });

    var _body = $('body');

    var rejectUrl = _body.attr('data-reject-url');
    var approvalUrl = _body.attr('data-approval-url');

    var table = $('#withdrawTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": _body.attr('data-url'),
            "data": function (d) {
                return $.extend({}, d, extendData());
            }
        },
        "ordering": true,
        "lengthChange": false,
        "searching": false,
        "colReorder": true,
        "columns": [
            {
                "title": "编号",
                "data": "id",
                "name": "id"
            },
            {
                "title": "合伙人",
                "data": "user",
                "name": "user"
            },
            {
                "title": "合伙人级别",
                "data": "userLevel",
                "name": "userLevel"
            },
            {
                "title": "收款人",
                "data": "payee",
                "name": "payee"
            },
            {
                "title": "开户行",
                "data": "bank",
                "name": "bank"
            },
            {
                "title": "帐号",
                "data": "account",
                "name": "account"
            },
            {
                "title": "收款人手机",
                "data": "mobile",
                "name": "mobile"
            },
            {
                "title": "提现金额",
                "data": "amount",
                "name": "amount"
            },
            {
                "title": "转账金额",
                "data": "actualAmount",
                "name": "actualAmount"
            },
            {
                "title": "发票物流",
                "data": "logisticsCompany",
                "name": "logisticsCompany"
            },
            {
                "title": "发票物流单号",
                "data": "logisticsCode",
                "name": "logisticsCode"
            },
            {
                "title": "状态",
                "name": "status",
                "data": "status"
            },
            {
                "title": "申请时间",
                "data": "requestTime",
                "name": "requestTime"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    if (item.statusCode == 2)
                        return '<a href="javascript:;" class="js-makeApproval" data-id="' + item.id + '"><i class="fa fa-check-circle"></i>&nbsp;批准</a>'
                            + '<a href="javascript:;" class="js-makeRefuse" data-id="' + item.id + '"><i class="fa fa-times-circle"></i>&nbsp;拒绝</a>';
                    return '';
                }
            },
            {
                "title": "备注",
                "data": "comment",
                "name": "comment"
            },
            {
                "title": "转账单据号",
                "data": "transactionRecordNumber",
                "name": "transactionRecordNumber"
            }
        ],
        "displayLength": 15,
        "drawCallback": function () {
            clearSearchValue();
        },
        "dom": "<'row'<'col-sm-12'B>>" +
        "<'row'<'col-sm-12'tr>>" +
        "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        "buttons": [{
            "extend": "excel",
            "text": "导出 Excel",
            "className": "btn-success btn-xs",
            "exportOptions": {
                "columns": ":not(.table-action)"
            }
        }, {
            "extend": 'colvis',
            "text": "筛选列",
            "className": "btn-success btn-xs"
        }]
    });

    $._table = table;

    function openRegion(workRegion, id, workUrl) {
        layer.open({
            content: workRegion.html(),
            area: ['500px', 'auto'],
            btn: ['确认', '取消'],
            zIndex: 9999,
            success: function (layerUi) {
            },
            yes: function (index, layerUi) {
                var value = getValue(layerUi);
                if (!value)
                    return;
                value.id = id;
                if (value) {
                    $.ajax(workUrl, {
                        method: 'post',
                        data: value,
                        success: function () {
                            table.ajax.reload();
                            layer.close(index);
                        }, error: function () {
                            layer.msg('服务端异常');
                        }
                    });
                }
            }
        });
    }

    $(document).on('click', '.js-search', function () {
        // 点击搜索方法。但如果数据为空，是否阻止
        table.ajax.reload();
    }).on('click', '.js-makeApproval', function () {
        var id = $(this).data('id');
        openRegion($('#J_makeApproval'), id, approvalUrl);
    }).on('click', '.js-makeRefuse', function () {
        var id = $(this).data('id');
        openRegion($('#J_makeRefuse'), id, rejectUrl);
    });

    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        table.ajax.reload();
    });

    // 添加额外的参数
    function extendData() {
        var formItem = $('.js-selectToolbar').find('.form-control');
        if (formItem.length === 0) return {};
        var data = {};

        formItem.each(function () {
            var t = $(this);
            var n = t.attr('name');
            var v = t.val();
            if (v) data[n] = v;
        });
        // 获取当前tab
        data['status'] = $('.js-orderStatus').find('.active').find('a').attr('data-status');
        return data;
    }

    function clearSearchValue() {
        //TODO
    }

    function getValue(ele) {
        var data = {};
        var inputs = ele.find('input');
        inputs.each(function () {
            if ($(this).val()) {
                data[$(this).attr('name')] = $(this).val();
            } else {
                layer.tips('请填写该值', $(this), {
                    tipsMore: true
                });
            }
        });
        return inputs.length === Object.keys(data).length ? data : false;
    }
});