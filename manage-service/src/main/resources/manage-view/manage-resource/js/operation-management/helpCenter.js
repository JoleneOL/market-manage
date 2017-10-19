$(function () {
    "use strict";

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
                "title": "类型", "data": "type", "name": "type"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-edit" data-id="' + item.id + '"><i class="fa fa-pencil-square-o"></i>&nbsp;编辑</a>';
                    var b = '<a href="javascript:;" class="js-del" data-id="' + item.id + '"><i class="fa fa-trash-o""></i>&nbsp;删除</a>';
                    return a + b;
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
        window.location.href = '_helpDetail.html' + '?id=' + $(this).data('id');
    }).on('click', '.js-del', function () {
        var id = $(this).data('id');
        $.ajax('/help/entry/' + id, {
            method: 'delete',
            success: function () {
                table.ajax.reload();
            },
            error: function () {
                layer.msg('服务器异常');
            }
        });
    });
});