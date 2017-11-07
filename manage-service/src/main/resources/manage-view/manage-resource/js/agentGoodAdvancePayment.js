$(function () {
    "use strict";

    var newLoginInput = $("#loginInput");
    newLoginInput.makeRecommendSelect();

    var newDateInput = $('input[name=date]');
    newDateInput.flatpickr({
        maxDate: new Date(),
        defaultDate: new Date(),
        locale: 'zh'
    });


    $('#J_datePicker').flatpickr({
        maxDate: new Date(),
        locale: 'zh'
    });

    var _body = $('body');

    var makeRefuse = $('#J_makeRefuse');
    var rejectUrl = _body.attr('data-reject-url');
    var approvalUrl = _body.attr('data-approval-url');

    var table = $('#paymentTable').DataTable({
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
                "title": "代理商",
                "data": "user",
                "name": "user"
            },
            {
                "title": "手机",
                "data": "mobile",
                "name": "mobile"
            },
            {
                "title": "金额",
                "data": "amount",
                "name": "amount"
            },
            {
                "title": "状态",
                "data": "status",
                "name": "status"
            },
            {
                "title": "单据号",
                "data": "serial",
                "name": "serial"
            },
            {
                "title": "货款余额",
                "name": "balance",
                "orderable": false,
                data: function (item) {
                    return '<a href="javascript:;" class="text-navy js-journal" data-id="' + item.loginId + '"><i class="fa fa-rmb"></i> ' + item.balance + '</a>';
                }
            },
            {
                "title": "时间",
                "data": "happenTime",
                "name": "happenTime"
            },
            {
                "title": "操作者",
                "data": "operator",
                "name": "operator",
                "orderable": false
            },
            {
                "title": "批准者",
                "data": "approval",
                "name": "approval",
                "orderable": false
            },
            {
                "title": "备注",
                "data": "comment",
                "name": "comment"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    // var journal = '<a href="javascript:;" class="js-journal" data-id="' + item.loginId + '"><i class="fa fa-dashboard"></i>&nbsp;流水</a>';
                    var journal = '';
                    if (item.approved == null)
                        return journal + '<a href="javascript:;" class="js-makeApproval" data-id="' + item.id + '"><i class="fa fa-check-circle"></i>&nbsp;批准</a>'
                            + '<a href="javascript:;" class="js-makeRefuse" data-id="' + item.id + '"><i class="fa fa-times-circle"></i>&nbsp;拒绝</a>';
                    return journal;
                }
            }
        ],
        "columnDefs": [
            {
                "targets": [10],
                "visible": makeRefuse.length > 0
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


    $('#addButton').click(function () {
        var newAmountInput = $('input[name=amount]');
        if (!newLoginInput.val()) {
            layer.msg('请选择代理商');
            return;
        }
        var amount = +newAmountInput.val();
        if (amount <= 0) {
            layer.msg('请输入预付金额');
            return;
        }

        $.ajax(_body.attr('data-url'), {
            method: 'post',
            data: {
                login: newLoginInput.val(),
                amount: amount,
                date: newDateInput.val(),
                serial: $('input[name=serial]').val()
            },
            success: function () {
                table.ajax.reload();
            }, error: function () {
                layer.msg('服务端异常');
            }
        })
    });


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
        table.ajax.reload();
    }).on('click', '.js-journal', function () {
        var loading = layer.load();
        $.ajax(_body.data('journal-url') + "?id=" + $(this).data('id'), {
            method: 'get',
            dataType: 'html',
            success: function (html) {
                layer.close(loading);
                layer.open({
                    content: html,
                    area: ['500px', '500px'],
                    // btn: ['确认', '取消'],
                    zIndex: 9999
                });
            }, error: function () {
                layer.close(loading);
                layer.msg('服务端异常');
            }
        });
    }).on('click', '.js-makeApproval', function () {
        var id = $(this).data('id');
        openRegion($('#J_makeApproval'), id, approvalUrl);
    }).on('click', '.js-makeRefuse', function () {
        var id = $(this).data('id');
        openRegion(makeRefuse, id, rejectUrl);
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