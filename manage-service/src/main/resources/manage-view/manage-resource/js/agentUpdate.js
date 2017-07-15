/**
 * Created by Chang on 2017/7/14.
 */
$(function () {
    "use strict";

    var table = $('#updateTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "/update/agent",
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
                "title": "姓名", "data": "name", "name": "name"
            },
            {
                "title": "目前代理级别", "data": "currentLevel", "name": "currentLevel"
            },
            {
                "title": "申请代理级别", "data": "applicationLevel", "name": "applicationLevel"
            },
            {
                "title": "地址", "data": "address", "name": "address"
            },
            {
                "title": "手机号", "data": "mobile", "name": "mobile"
            },
            {
                "title": "身份证",
                "name": "IDCard",
                data: function (item) {
                    var a = '<img class="img-small js-feedback-big" src="' + item.IDCard.cardFront + '">';
                    var b = '<img class="img-small js-feedback-big" src="' + item.IDCard.cardBack + '">';
                    return a + b;
                }
            },
            {
                "title": "营业证",
                "name": "businessLicense",
                data: function (item) {
                    if (item.businessLicense)
                        return '<img class="img-small js-feedback-big" src="' + item.businessLicense + '">';

                    return '';
                }
            },
            {
                "title": "升级费用", "data": "applicationCost", "name": "applicationCost"
            },
            {
                "title": "费用类型", "data": "applicationType", "name": "applicationType"
            },
            {
                "title": "时间", "data": "applicationDate", "name": "applicationDate"
            },
            {
                "title": "操作人", "data": "operator", "name": "operator"
            },
            {
                "title": "操作",
                "className": 'table-action',
                "orderable": false,
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-edit" data-id="' + item.id + '"><i class="fa fa-edit"></i>&nbsp;修改</a>';
                    if (item.applicationLevel === '省代理')
                        a += '<a href="javascript:;" class="js-custom" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;通过</a>';
                    else
                        a += '<a href="javascript:;" class="js-agree" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;通过</a>';
                    if (item.stateCode === 0)
                        return a;
                    return '';
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
        }, {
            "extend": 'colvis',
            "text": "筛选列",
            "className": "btn-xs"
        }]
    });

    $(document).on('click', '.js-search', function () {
        // 点击搜索方法。但如果数据为空，是否阻止
        table.ajax.reload();
    }).on('click', '.js-edit', function () {
        // 需要获取一些参数供详情跳转
        window.location.href = '_agentUpdateEdit.html' + '?id=' + $(this).data('id');
    }).on('click', '.js-agree', function () {
        var id = $(this).data('id');
        layer.confirm('通过申请？', {
            btn: ['通过', '取消'] //按钮
        }, function (index) {
            $.ajax('/agent/' + id + '/update', {
                method: 'put',
                success: function (res) {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                }
            });
        });
    }).on('click', '.js-custom', function () {
        var id = $(this).data('id');
        layer.prompt({
            title: '自定义代理级别',
            formType: 1
        }, function (pass, index) {
            $.ajax('/login/' + id + '/disable', {
                method: 'put',
                data: {customLevel: pass},
                success: function () {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                }
            });
        });
    }).on('click', '.js-feedback-big', function () {
        var $img = $('<img class="img-feedback-big img-thumbnail"/>').attr('src', $(this).attr('src'));
        var content = $('<div class="container">').append($img);
        layer.open({
            type: 1,
            shade: 0.5,
            title: false,
            shadeClose: true,
            area: ['auto', 'auto'],
            content: content.html()
        });
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