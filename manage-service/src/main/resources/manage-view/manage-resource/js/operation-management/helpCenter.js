$(function () {
    "use strict";
    var _body = $('body');
    var table = $('#helpTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": $('body').attr('data-url'),
        "ordering": true,
        "lengthChange": false,
        "searching": false,
        "colReorder": true,
        "columns": [
            {
                "title": "标题", "data": "title", "name": "title"
            },
            {
                "title": "状态", "data": "enableLabel", "name": "enableLabel",
            },
            {
                "title": "首页展示状态", "data": "isWeightLabel", "name": "isWeightLabel",
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-edit" data-id="' + item.id + '"><i class="fa fa-pencil-square-o"></i>&nbsp;编辑</a>';
                    if (item.enable) {
                        a += '<a href="javascript:;" class="js-disable" data-id="' + item.id + '" ><i class="fa fa-lock"></i>&nbsp;禁用</a>';
                    }
                    else
                        a += '<a href="javascript:;" class="js-enable" data-id="' + item.id + '"><i class="fa fa-unlock"></i>&nbsp;启用</a>';
                    if (item.isWeight) {
                        a += '<a href="javascript:;" class="js-notWeightLabel" data-id="' + item.id + '" ><i class="fa fa-lock"></i>&nbsp;隐藏</a>';
                    }
                    else
                        a += '<a href="javascript:;" class="js-isWeightLabel" data-id="' + item.id + '"><i class="fa fa-unlock"></i>&nbsp;展示</a>';
                    return a;
                }
            }
        ],
        "displayLength": 15,
        "drawCallback": function () {
            // clearSearchValue();
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

    $(document).on('click', '.js-edit', function () {
        window.location.href = _body.data('edit-url') + '?id=' + $(this).data('id');
    }).on('click', '.js-disable', function () {
        var id = $(this).data('id');
        $.ajax('/help/' + id + '/disable', {
            method: 'put',
            success: function () {
                table.ajax.reload();
            },
            error: function () {
                layer.msg('服务器异常');
            }
        });
    }).on('click', '.js-enable', function () {
        var id = $(this).data('id');
        $.ajax('/help/' + id + '/enable', {
            method: 'put',
            success: function () {
                table.ajax.reload();
            },
            error: function () {
                layer.msg('服务器异常');
            }
        })
    }).on('click', '.js-notWeightLabel', function () {
        var id = $(this).data('id');
        $.ajax('/help/' + id + '/notWeightLabel', {
            method: 'put',
            success: function () {
                table.ajax.reload();
            },
            error: function () {
                layer.msg('服务器异常');
            }
        })
    }).on('click', '.js-isWeightLabel', function () {
        var id = $(this).data('id');
        $.ajax('/help/' + id + '/isWeightLabel', {
            method: 'put',
            success: function () {
                table.ajax.reload();
            },
            error: function () {
                layer.msg('服务器异常');
            }
        })
    });
});