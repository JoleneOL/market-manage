/**
 * Created by Chang on 2017/7/14.
 */
$(function () {
    "use strict";

    var table = $('#updateTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "/manage/promotionRequests",
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
                "title": "姓名", "data": "name", "name": "name", "orderable": false
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
                // "name": "IDCard",
                "orderable": false,
                data: function (item) {
                    var a = '<img class="img-small js-feedback-big" src="' + item.cardFront + '">';
                    var b = '<img class="img-small js-feedback-big" src="' + item.cardBack + '">';
                    return a + b;
                }
            },
            {
                "title": "营业证",
                "name": "businessLicense",
                "orderable": false,
                data: function (item) {
                    if (item.businessLicense)
                        return '<img class="img-small js-feedback-big" src="' + item.businessLicense + '">';

                    return '';
                }
            },
            // {
            //     "title": "升级费用", "data": "applicationCost", "name": "applicationCost"
            // },
            {
                "title": "线上支付", "data": "paymentStatus", "name": "paymentStatus"
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
                    a += '<a href="javascript:;" class="js-rejected" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;拒绝</a>';
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

        table.ajax.reload();
    }).on('click', '.js-edit', function () {
        // 需要获取一些参数供详情跳转
        window.location.href = '_agentUpdateEdit.html' + '?id=' + $(this).data('id');
    }).on('click', '.js-rejected', function () {
        var id = $(this).data('id');
        layer.prompt({
            formType: 2,
            value: '请填写拒绝原因',
            title: '拒绝申请？',
            area: ['300px', '100px'], //自定义文本域宽高,
            //btn: ['拒绝', '取消'] //按钮
        },function(value, index, elem){
            $.ajax('/manage/promotionRequests/' + id +'/rejected',{
                method: 'put',
                contentType : 'text/plain; charset=UTF-8',
                data : value,
                success: function (res) {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                }
            });
        });
        /*layer.confirm('拒绝申请？', {

            btn: ['拒绝', '取消'] //按钮
        }, function (index) {
            $.ajax('/manage/promotionRequests/' + id + '/rejected', {
                method: 'put',
                success: function (res) {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                }
            });
        });*/
    }).on('click', '.js-agree', function () {
        var id = $(this).data('id');
        layer.confirm('通过申请？', {
            btn: ['通过', '取消'] //按钮
        }, function (index) {
            $.ajax('/manage/promotionRequests/' + id + '/approved', {
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

        function doPromotionRequests(title, index) {
            $.ajax('/manage/promotionRequests/' + id + '/approved', {
                method: 'put',
                contentType: 'text/plain;charset=UTF-8',
                data: title,
                success: function () {
                    table.ajax.reload();
                    if (index)
                        layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                    if (index)
                        layer.close(index);
                }
            });
        }

        layer.confirm('是否设置自定义代理级别', {icon: 3, title: '提示'}, function (index) {
            //do something
            layer.prompt({
                title: '自定义代理级别',
                formType: 0
            }, doPromotionRequests);
            layer.close(index);
        }, function (index) {
            //do something
            doPromotionRequests(null);
            layer.close(index);
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