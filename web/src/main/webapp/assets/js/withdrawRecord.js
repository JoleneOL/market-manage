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
                "title": "提现金额", "data": "amount"
            },
            {
                "title": "收款帐号", "data": "account"
            },
            {
                "title": "收款人", "data": "user"
            },
            {
                "title": "发票物流", "data": "logistics"
            },
            {
                "title": "提现时间", "data": "time"
            },
            {
                "title": "状态", "data": "status"
            },
            {
                "title": "备注", "data": "remark"
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