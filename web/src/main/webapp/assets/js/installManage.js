/**
 * Created by Chang on 2017/4/30.
 */
$(function () {
    "use strict";

    var table = $('#installTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "mock/installData.json",
            "data": function (d) {
                return $.extend({}, d, extendData());
            }
        },
        "ordering": false,
        "dom": "<'row'<'col-sm-12'<'js-selectToolbar'>>>" +
        "<'row'<'col-sm-12'tr>>" +
        "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        "columns": [
            {
                "title": "安装机型", "data": "type"
            },
            {
                "title": "饮水机编码", "data": "code"
            },
            {
                "title": "安装时间", "data": "installTime"
            },
            {
                "title": "安装地址", "data": "address"
            },
            {
                "title": "使用用户", "data": "user"
            },
            {
                "title": "手机号", "data": "phone"
            },
            {
                "title": "状态", "data": "state"
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
    }).on('click', '.js-checkLogistics', function () {
        // TODO
        // 需要获取一些参数供详情跳转
        $('#content', parent.document).attr('src', 'logisticsDetail.html');
    });

    $('.js-orderStatus').find('a').click(function () {
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