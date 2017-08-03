$(function () {
    "use strict";

    var _body = $('body');
    var appealUrl = _body.attr('data-appeal-url');

    var table = $('#mortgageTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": _body.attr('data-url')
        },
        "ordering": true,
        "lengthChange": false,
        "searching": false,
        "colReorder": true,
        "columns": [
            {
                "title": "订单编号", "data": "orderId", "name": "orderId"
            },
            {
                "title": "按揭码", "data": "mortgageCode", "name": "mortgageCode"
            },
            {
                "title": "用户姓名", "data": "userName", "name": "userName"
            },
            {
                "title": "手机号", "data": "mobile", "name": "mobile"
            },
            {
                "title": "下单时间", "data": "orderTime", "name": "orderTime"
            },
            {
                "title": "状态",
                "name": "status",
                data: function (item) {
                    if(item.statusCode === 4) return '<span class="text-danger">' + item.status + '</span>';
                    return item.status
                }
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    if(item.statusCode === 4)
                        return '<a href="javascript:;" class="js-info" data-id="' + item.id + '" data-from="2" data-status="' + item.statusCode + '"><i class="fa fa fa-check-circle-o"></i>&nbsp;重新申请信审</a>';
                    if (item.statusCode === 2)
                        return '<a href="javascript:;" class="js-info" data-id="' + item.id + '" data-from="2" data-status="' + item.statusCode + '"><i class="fa fa fa-check-circle-o"></i>&nbsp;申请信审</a>';
                    return '';
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

    $(document).on('click', '.js-info', function () {
        // window.location.href = '_orderDetail.html?id=' + $(this).data('id') + '&from=' + $(this).data('from') + '&status=' + $(this).data('status');
        window.location.href = appealUrl + '?id=' + $(this).data('id');
    });

    $('#J_datePicker').flatpickr({
        maxDate: new Date(),
        locale: 'zh'
    });
});