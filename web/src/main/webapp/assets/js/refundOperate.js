/**
 * Created by Neo on 2017/5/6.
 */
$(function () {
    "use strict";

    var table = $('#refundTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "mock/refundOperate.json",
            "data": function (d) {
                return $.extend({}, d, extendData());
            }
        },
        "ordering": false,
        "lengthChange": false,
        "searching": false,
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
                "title": "发起时间", "data": "time", "name": "time"
            },
            {
                "title": "操作员", "data": "operator", "name": "operator"
            },
            {
                "title": "状态", "data": "status", "name": "status"
            },
            {
                title: "操作",
                className: 'table-operate',
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-checkUser" data-id="' + item.id + '"><i class="fa fa-check-circle-o" aria-hidden="true"></i>&nbsp;退款详情</a>';
                    var b = '<a href="javascript:;" class="js-checkLogistics" data-id="' + item.id + '"><i class="fa fa-check-circle-o" aria-hidden="true"></i>&nbsp;查看物流</a>';
                    if(item.statusCode === 2)  return a + b;
                    return a;
                }
            }
        ],
        "displayLength": 15,
        "drawCallback": function () {
            clearSearchValue();
        }
    });

    $(document).on('click', '.js-search', function () {
        // 点击搜索方法。但如果数据为空，是否阻止
        table.ajax.reload();
    }).on('click', '.js-checkUser', function () {
        // TODO
        // 需要获取一些参数供详情跳转
        $('#content', parent.document).attr('src', 'repairDetail.html');
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
});