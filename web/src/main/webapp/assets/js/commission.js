/**
 * Created by Chang on 2017/5/1.
 */
$(function () {
    "use strict";

    DatePicker('#beginDate', '#endDate');

    var $dataRange = $('#J_dataRange');
    $('#J_dataSelect').change(function () {
        if ($(this).val() === '5') {
            $dataRange.removeClass('hidden');
        } else {
            $dataRange.addClass('hidden');
        }
    });

    var table = $('#commTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "mock/commData.json",
            "data": function (d) {
                return $.extend({}, d, extendData());
            }
        },
        "ordering": false,
        "lengthChange": false,
        "filter": false,
        "columns": [
            {
                "title": "佣金类型", "data": "commType"
            },
            {
                "title": "订单编号", "data": "code"
            },
            {
                "title": "购买人姓各", "data": "user"
            },
            {
                "title": "订单总额", "data": "orderTotal"
            },
            {
                "title": "分成比例", "data": "divided"
            },
            {
                "title": "佣金", "data": "commission"
            },
            {
                "title": "获佣时间", "data": "commTime"
            }
        ],
        "displayLength": 15,
        "drawCallback": function () {
            clearSearchValue();
        },
        "buttons": [
            {
                'extend': 'excel',
                'text': '导出',//定义导出excel按钮的文字
                'exportOptions': {
                    'modifier': {
                        'page': 'current'
                    }
                }
            }
        ]
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

function DatePicker(beginSelector, endSelector) {
    // 仅选择日期
    $(beginSelector).datepicker(
        {
            language: "zh-CN",
            autoclose: true,
            format: "yyyy-mm-dd",
            clearBtn: true,
            endDate: new Date()
        }).on('changeDate', function (ev) {
        if (ev.date) {
            $(endSelector).datepicker('setStartDate', new Date(ev.date.valueOf()))
        } else {
            $(endSelector).datepicker('setStartDate', null);
        }
    });
    $(endSelector).datepicker(
        {
            language: "zh-CN",
            autoclose: true,
            format: "yyyy-mm-dd",
            clearBtn: true,
            endDate: new Date()
        }).on('changeDate', function (ev) {
        if (ev.date) {
            $(beginSelector).datepicker('setEndDate', new Date(ev.date.valueOf()))
        } else {
            $(beginSelector).datepicker('setEndDate', new Date());
        }
    });
}