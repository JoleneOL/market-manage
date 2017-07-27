/**
 * Created by Chang on 2017/4/25.
 */

$(function () {
    "use strict";

    var table = $('#usersTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "mock/userData.json",
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
                "title": "用户", "data": "name", "name": "name"
            },
            {
                "title": "级别", "data": "rank", "name": "rank"
            },
            {
                "title": "推荐人数", "data": "recommend", "name": "recommend"
            },
            {
                "title": "注册时间", "data": "joinTime", "name": "joinTime"
            },
            {
                "title": "省", "data": "province", "name": "province"
            },
            {
                "title": "市", "data": "city", "name": "city"
            },
            {
                "title": "区 / 县", "data": "district", "name": "district"
            },
            {
                "title": "详细地址", "data": "address", "name": "address"
            },
            {
                "title": "手机号码", "data": "phone", "name": "phone"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    return '<a href="javascript:;" class="js-checkUser" data-id="' + item.id + '"><i class="fa fa-check-circle-o" aria-hidden="true"></i>&nbsp;查看</a>';
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

    $(document).on('click', '.js-search', function () {

        table.ajax.reload();
    }).on('click', '.js-checkUser', function () {
        $('#content', parent.document).attr('src', 'userDetail.html');
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