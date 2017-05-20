/**
 * Created by Chang on 2017/5/1.
 */
$(function () {
    "use strict";

    var table = $('#withdrawTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": "mock/withdraw.json",
        "ordering": false,
        "lengthChange": false,
        "searching": false,
        "columns": [
            {
                "title": "提现金额", "data": "amount", "name": "amount"
            },
            {
                "title": "收款帐号", "data": "account", "name": "account"
            },
            {
                "title": "收款人", "data": "user", "name": "user"
            },
            {
                "title": "发票物流", "data": "logistics", "name": "logistics"
            },
            {
                "title": "提现时间", "data": "time", "name": "time"
            },
            {
                "title": "状态", "data": "status", "name": "status"
            },
            {
                "title": "备注", "data": "remark", "name": "remark"
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