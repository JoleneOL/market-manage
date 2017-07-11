/**
 * Created by Neo on 2017/7/4.
 */
$(function () {
    "use strict";

    var table = $('#productTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "/product/list",
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
                "title": "产品品类", "data": "category", "name": "category"
            },
            {
                "title": "型号", "data": "type", "name": "type"
            },
            {
                "title": "厂家", "data": "supplier", "name": "supplier"
            },
            {
                "title": "单价（元）", "data": "price", "name": "price"
            },
            {
                "title": "使用费（元）", "data": "cost", "name": "cost"
            },
            {
                "title": "安装费（元）", "data": "installFee", "name": "installFee"
            },
            {
                "title": "分期数", "data": "stagesTime", "name": "stagesTime"
            },
            {
                "title": "分期产品", "data": "stagesType", "name": "stagesType"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-checkInfo" data-id="' + item.id + '"><i class="fa fa-check-circle-o" aria-hidden="true"></i>&nbsp;详情</a>';
                    var b = '<a href="javascript:;" class="js-delete" data-id="' + item.id + '"><i class="fa fa-trash-o" aria-hidden="true"></i>&nbsp;删除</a>';
                    return a + b;
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
    }).on('click', '.js-checkInfo', function () {
        // TODO
        // 需要获取一些参数供详情跳转
        window.location.href = '_productDetail.html?id=' + $(this).data('id');
    }).on('click', '.js-delete', function () {
        var id = $(this).data('id');
        layer.confirm('确定删除该产品？', {
            btn: ['确定', '取消'] //按钮
        }, function (index) {
            $.ajax('/products/' + id, {
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