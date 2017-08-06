$(function () {
    var table = $('#goodsTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $('body').data('url'),
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
                "title": "商品名称", "data": "goodsName", "name": "goodsName"
            },
            {
                "title": "关联货品", "data": "productName", "name": "productName"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-checkInfo" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;修改</a>';
                    if(item.onSale) {
                        a += '<a href="javascript:;" class="js-offSale" data-id="' + item.id + '"><i class="fa fa-arrow-circle-down"></i>&nbsp;下架</a>';
                    } else {
                        a += '<a href="javascript:;" class="js-onSale" data-id="' + item.id + '"><i class="fa fa-arrow-circle-up"></i>&nbsp;上架</a>';
                    }

                    var b = '<a href="javascript:;" class="js-delete" data-id="' + item.id + '"><i class="fa fa-trash-o"></i>&nbsp;删除</a>';
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

        table.ajax.reload();
    }).on('click', '.js-checkInfo', function () {
        window.location.href = '_goodsOperate.html?id=' + $(this).data('id');
    }).on('click', '.js-delete', function () {
        var id = $(this).data('id');
        layer.confirm('确定删除商品？', {
            btn: ['确定', '取消']
        }, function (index) {
            $.ajax('/goods/' + id, {
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
    }).on('click', '.js-offSale', function () {
        var id = $(this).data('id');
        layer.confirm('确定下架该商品？', {
            btn: ['确定', '取消']
        }, function (index) {
            $.ajax('/goods/' + id + '/off', {
                method: 'put',
                success: function () {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                }
            });
        });
    }).on('click', '.js-onSale', function () {
        var id = $(this).data('id');
        layer.confirm('确定上架该商品？', {
            btn: ['确定', '取消']
        }, function (index) {
            $.ajax('/goods/' + id + '/on', {
                method: 'put',
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