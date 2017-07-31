$(function () {
    "use strict";

    var table = $('#categoryTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $('body').data('url'),
            "data": function (d) {
                return d;
            }
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
                    if (item.enable)
                        return '<a href="javascript:;" class="js-disableDepot" data-id="' + item.id + '"><i class="fa fa fa-pencil-square-o" aria-hidden="true"></i>&nbsp;禁用</a>';
                    return '<a href="javascript:;" class="js-enableDepot" data-id="' + item.id + '"><i class="fa fa fa-pencil-square-o" aria-hidden="true"></i>&nbsp;启用</a>';
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

    $(document).on('click', '.js-disableDepot', function () {
        $.ajax(dataUrl + '/' + $(this).attr('data-id') + "/disable", {
            method: 'put',
            success: function () {
                table.ajax.reload();
            }
        });
    }).on('click', '.js-enableDepot', function () {
        $.ajax(dataUrl + '/' + $(this).attr('data-id') + "/enable", {
            method: 'put',
            success: function () {
                table.ajax.reload();
            }
        });
    });
});