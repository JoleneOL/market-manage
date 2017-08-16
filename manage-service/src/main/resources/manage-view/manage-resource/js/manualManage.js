$(function () {
    var detailUrl = $('body').attr('data-detail-url');
    var table = $('#manualTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "/manual/list",
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
                "title": "订单号", "data": "orderId", "name": "orderId"
            },
            {
                "title": "产品及型号",
                "orderable": false,
                data: function (item) {
                    return item.goods + ' / ' + item.model;
                }
            },
            {
                "title": "下单数量", "data": "amount", "name": "amount"
            },
            {
                "title": "订单金额", "data": "total", "name": "total"
            },
            {
                "title": "收货人", "data": "orderUser", "name": "orderUser"
            },
            {
                "title": "收货地址", "data": "address", "name": "address"
            },
            {
                "title": "手机号", "data": "mobile", "name": "mobile"
            },
            {
                "title": "订单时间", "data": "orderTime", "name": "orderTime"
            },
            {
                "title": "状态", "data": "status", "name": "status"
            },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    // var b = '<a href="javascript:;" class="js-edit" data-id="' + item.id + '"><i class="fa fa-pencil-square-o"></i>&nbsp;修改</a>';
                    // if (item.statusCode === 2)
                    //     a += b;
                    return '<a href="javascript:;" class="js-info" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;查看</a>';
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
        },{
            "extend": 'colvis',
            "text": "筛选列",
            "className": "btn-xs"
        }]
    });

    $(document).on('click', '.js-search', function () {
        table.ajax.reload();
    }).on('click', '.js-info', function () {
        window.location.href = detailUrl + '?id=' + $(this).data('id');
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

    $('#J_datePicker').flatpickr({
        maxDate: new Date(),
        locale: 'zh'
    });
});