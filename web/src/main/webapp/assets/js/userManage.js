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
        "ordering": false,
        "lengthChange": false,
        "searching": false,
        "columns": [
            {
                "title": "用户", "data": "name"
            },
            {
                "title": "级别", "data": "rank"
            },
            {
                "title": "推荐人数", "data": "recommend"
            },
            {
                "title": "注册时间", "data": "joinTime"
            },
            {
                "title": "省", "data": "province"
            },
            {
                "title": "市", "data": "city"
            },
            {
                "title": "区 / 县", "data": "district"
            },
            {
                "title": "详细地址", "data": "address"
            },
            {
                "title": "手机号码", "data": "phone"
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
        }
    });

    $(document).on('click', '.js-search', function () {
        // 点击搜索方法。但如果数据为空，是否阻止
        table.ajax.reload();
    }).on('click', '.js-checkUser', function () {
        // TODO
        // 需要获取一些参数供详情跳转
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