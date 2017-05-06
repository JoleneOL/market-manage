/**
 * Created by Chang on 2017/5/1.
 */
$(function () {
    "use strict";

    var table = $('#refundTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": "mock/refundRecord.json",
        "ordering": false,
        "lengthChange": false,
        "searching": false,
        "columns": [
            {
                "title": "佣金类型", "data": "type", "name": "type"
            },
            {
                "title": "订单编号", "data": "orderId", "name": "orderId"
            },
            {
                "title": "购买人姓名", "data": "user", "name": "user"
            },
            {
                "title": "订单总额", "data": "total", "name": "total"
            },
            {
                "title": "分成比例", "data": "divided", "name": "divided"
            },
            {
                "title": "佣金", "data": "commission", "name": "commission"
            },
            {
                "title": "获佣时间", "data": "time", "name": "time"
            }
        ],
        "displayLength": 15,
        "dom": "<'row'<'col-sm-12'B>>" +
        "<'row'<'col-sm-12'tr>>" +
        "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        "buttons": [{
            "extend": "excel",
            "text": "导出 Excel",
            "className": "btn-success btn-xs"
        }]
    });

});