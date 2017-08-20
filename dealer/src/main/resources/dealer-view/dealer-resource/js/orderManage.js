/**
 * Created by Neo on 2017/4/26.
 */
$(function () {
    "use strict";

    var _body = $('body');

    var quickUrl = _body.attr('data-quick-url');
    var allowMockPay = _body.attr('data-allow-mock-pay') === 'true';
    var allowSettlement = _body.attr('data-allow-settlement') === 'true';

    var table = $('#orderTable').DataTable({
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
                "title": "订单号", "data": "orderId", "name": "orderId"
            },
            {
                "title": "购买用户", "data": "orderUser", "name": "orderUser"
            },
            {
                "title": "手机号", "data": "phone", "name": "phone"
            },
            {
                "title": "商品品类", "data": "category", "name": "category"
            },
            {
                "title": "商品型号", "data": "type", "name": "type"
            },
            {
                "title": "数量", "data": "amount", "name": "amount"
            },
            {
                "title": "充值套餐", "data": "package", "name": "package"
            },
            {
                "title": "支付方式", "data": "method", "name": "method"
            },
            {
                "title": "订单总金额", "data": "total", "name": "total"
            },
            {
                "title": "安装地址", "data": "address", "name": "address"
            },
            {
                "title": "状态", "data": "status", "name": "status"
            },
            {
                "title": "下单时间", "data": "orderTime", "name": "orderTime"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-checkOrder" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;查看</a>';
                    var b = '<a href="javascript:;" class="js-modifyOrder" data-id="' + item.id + '"><i class="fa fa fa-pencil-square-o"></i>&nbsp;修改</a>';
                    if (item.statusCode === 0)
                        a = a + b;
                    var c = '<a href="javascript:;" class="js-quickDone" data-id="' + item.id + '"><i class="fa fa fa-pencil-square-o"></i>&nbsp;快速完成订单</a>';
                    if (item.quickDoneAble && quickUrl)
                        a = a + c;
                    var d = '<a href="javascript:;" class="js-mockPay" data-id="' + item.id + '"><i class="fa fa fa-pencil-square-o"></i>&nbsp;模拟支付</a>';
                    if (item.statusCode === 1 && allowMockPay)
                        a = a + d;
                    var e = '<a href="javascript:;" class="js-settlement" data-id="' + item.id + '"><i class="fa fa fa-pencil-square-o" ></i>&nbsp;重新结算</a>';
                    if (item.statusCode !== 0 && item.statusCode !== 1 && allowSettlement)
                        a = a + e;
                    // 模拟支付
                    var f = '<a href="javascript:;" class="btn btn-primary btn-xs js-dispatch" data-id="' + item.id + '">派单</a>';
                    if (item.statusCode === 6)
                        a = a + f;
                    return a;
                }
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
        },{
            "extend": 'colvis',
            "text": "筛选列",
            "className": "btn-success btn-xs"
        }]
    });

    $._table = table;

    var detailForm = $('#detailForm');

    $(document).on('click', '.js-search', function () {

        table.ajax.reload();
    }).on('click', '.js-checkOrder', function () {
        $('input[name=id]', detailForm).val($(this).attr('data-id'));
        detailForm.submit();
    }).on('click', '.js-settlement', function () {
        // 重新接收端
        $.ajax('/orderData/settlement/' + $(this).attr('data-id'), {
            method: 'put',
            success: function () {
                alert('重新结算完成');
            }
        });
    }).on('click', '.js-mockPay', function () {
        // 模拟支付
        if (!allowMockPay) {
            alert('不支持');
            return;
        }
        $.ajax('/orderData/mockPay/' + $(this).attr('data-id'), {
            method: 'put',
            success: function () {
                table.ajax.reload();
            }
        });
    }).on('click', '.js-quickDone', function () {

        if (!quickUrl) {
            alert('不支持');
            return;
        }
        $.ajax(quickUrl + $(this).attr('data-id'), {
            method: 'put',
            success: function () {
                table.ajax.reload();
            }
        });
    }).on('click', '.js-modifyOrder', function () {
        // $('#content', parent.document).attr('src', 'orderModify.html');
    });
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        table.ajax.reload();
    });
    // 添加额外的参数
    function extendData() {
        var formItem = $('.js-selectToolbar').find('.form-control');
        if (formItem.length === 0)  return {};
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

});