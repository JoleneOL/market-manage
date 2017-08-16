$(function () {
    "use strict";

    var table = $('#categoryTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $('body').data('url')
        },
        "ordering": true,
        "lengthChange": false,
        "searching": false,
        "colReorder": true,
        "columns": [
            {
                "title": "类目", "data": "category", "name": "category"
            },
            {
                "title": "关联商品", "data": "goods", "name": "goods"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {

                    var  a = '<a href="javascript:;" class="js-edit" data-id="' + item.id + '"><i class="fa fa-pencil-square-o" aria-hidden="true"></i>&nbsp;编辑</a>';
                    var b = '';
                    if (item.goods.length > 0)
                        b = '<a href="javascript:;" class="js-del" data-id="' + item.id + '" data-items="'+ item.goods.length +'"><i class="fa fa fa-trash-o" aria-hidden="true"></i>&nbsp;删除</a>';
                    else
                        b = '<a href="javascript:;" class="js-del" data-id="' + item.id + '"><i class="fa fa fa-trash-o" aria-hidden="true"></i>&nbsp;删除</a>';
                    return a + b;
                }
            }
        ],
        "displayLength": 15,
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

    $(document).on('click', '.js-edit', function () {
        var id = $(this).data('id');
        layer.prompt({
            title: '编辑类目名称',
            formType: 0
        }, function (pass, index) {
            $.ajax('/products/category/' + id, {
                method: 'put',
                contentType: 'text/plain;charset=UTF-8',
                data: pass,
                success: function () {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                    layer.close(index);
                }
            });
        });
    }).on('click', '.js-del', function () {
        var id = $(this).data('id');
        var len = $(this).data('items');
        var msg = len ? '该类目下有'+len+'个商品，<br>确定删除该类目？': '确定删除该类目';
        layer.confirm(msg, {
            btn: ['确定', '取消']
        }, function (index) {
            $.ajax('/products/category/' + id, {
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

    $('#J_addCategory').click(function() {
        layer.prompt({
            title: '添加类目',
            formType: 0
        }, function (pass, index) {
            $.ajax('/products/category/', {
                method: 'post',
                contentType: 'text/plain;charset=UTF-8',
                data: pass,
                success: function () {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                    layer.close(index);
                }
            });
        });
    });
});