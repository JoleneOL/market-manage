/**
 * Created by Neo on 2017/7/4.
 */
$(function () {
    "use strict";

    var _body = $('body');

    var table = $('#productTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": _body.data('url'),
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
                "title": "货品名称", "data": "productName", "name": "productName"
            },
            {
                "title": "类目", "data": "category", "name": "category"
            },
            {
                "title": "型号", "data": "code", "name": "code"
            },
            {
                "title": "品牌", "data": "brand", "name": "brand"
            },
            // {
            //     "title": "厂家", "data": "supplier", "name": "supplier"
            // },
            {
                "title": "设备款", "data": "price", "name": "price"
            },
            {
                "title": "服务费", "data": "installFee", "name": "installFee"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-checkInfo" data-id="' + item.code + '"><i class="fa fa-check-circle-o"></i>&nbsp;详情</a>';
                    var b = '<a href="javascript:;" class="js-delete" data-id="' + item.code + '"><i class="fa fa-trash-o"></i>&nbsp;禁用</a>';
                    var b2 = '<a href="javascript:;" class="js-active" data-id="' + item.code + '"><i class="fa fa-caret-square-o-up"></i>&nbsp;激活</a>';
                    var c = '<a href="javascript:;" class="js-pushHR" data-id="' + item.code + '"><i class="fa fa-cloud-upload"></i>&nbsp;推送给日日顺</a>';
                    if (item.enable)
                        return a + b + c;
                    return a + b2 + c;
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
    }).on('click', '.js-checkInfo', function () {
        // 需要url encoding
        window.location.href = _body.data('detail-url') + '?code=' + encodeURIComponent($(this).data('id'));
    }).on('click', '.js-pushHR', function () {
        var id = $(this).data('id');
        layer.confirm('确定要将货品作为物料推送至日日顺？', {
            btn: ['确定', '取消']
        }, function (index) {
            $.ajax('/products/' + encodeURIComponent(id) + "/haier", {
                method: 'put',
                success: function () {
                    table.ajax.reload();
                    layer.close(index);
                    layer.msg('成功推送');
                },
                error: function () {
                    layer.msg('服务器异常或者货品信息不完整');
                }
            });
        });
    }).on('click', '.js-active', function () {
        var id = $(this).data('id');
        $.ajax('/products/' + encodeURIComponent(id), {
            method: 'put',
            success: function () {
                table.ajax.reload();
            },
            error: function () {
                layer.msg('服务器异常');
            }
        });
    }).on('click', '.js-delete', function () {
        var id = $(this).data('id');
        layer.confirm('确定禁用货品？', {
            btn: ['确定', '取消']
        }, function (index) {
            $.ajax('/products/' + encodeURIComponent(id), {
                method: 'delete',
                success: function () {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                }
            });
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