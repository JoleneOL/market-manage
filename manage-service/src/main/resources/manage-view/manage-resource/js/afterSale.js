/**
 * Created by Neo on 2017/5/2.
 */
$(function () {
    "use strict";

    var table = $('#repairTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "/afterSale/list",
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
                "title": "购买用户", "data": "user", "name": "user"
            },
            {
                "title": "手机号", "data": "phone", "name": "phone"
            },
            {
                "title": "购买型号", "data": "type", "name": "type"
            },
            {
                "title": "商品编码", "data": "code", "name": "code"
            },
            {
                "title": "数量", "data": "amount", "name": "amount"
            },
            {
                "title": "维修时间", "data": "time", "name": "time"
            },
            {
                "title": "操作员", "data": "operator", "name": "operator"
            },
            {
                "title": "状态", "data": "status", "name": "status"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-checkUser" data-id="' + item.id + '"><i class="fa fa-check-circle-o" aria-hidden="true"></i>&nbsp;查看</a>';
                    var b = '<a href="javascript:;" class="btn btn-primary btn-xs js-dispatch" data-id="' + item.id + '">派单</a>';
                    if (item.statusCode === 0)
                        a += b;
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
            "className": "btn-xs",
            "exportOptions": {
                "columns": ":not(.table-action)"
            }
        },{
            "extend": 'colvis',
            "text": "筛选列",
            "className": "btn-xs"
        }]
    });

    $(document).on('click', '.js-search', function () {

        table.ajax.reload();
    }).on('click', '.js-checkUser', function () {
        window.location.href = '_repairDetail.html'
    }).on('click', '.js-dispatch', function () {
        window.location.href = '_repairOperate.html'
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
        return data;
    }

    function clearSearchValue() {
        //TODO
    }

    $('#J_datePicker').flatpickr({
        maxDate: new Date(),
        locale: 'zh'
    });
});