/**
 * Created by CJ on 03/08/2017.
 */

$(function () {
    "use strict";

    function getValue(ele) {
        var data = {};
        var inputs = ele.find('input');
        inputs.each(function () {
            if ($(this).val()) {
                data[$(this).attr('name')] = $(this).val();
            } else {
                layer.tips('请填写该值', $(this), {
                    tipsMore: true
                });
            }
        });
        return inputs.length === Object.keys(data).length ? data : false;
    }

    var productTable = $('#ProductTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": 'products',
        "ordering": false,
        "lengthChange": false,
        "searching": false,
        "colReorder": true,
        "columns": [
            {
                "title": "编码",
                "data": "code",
                "name": "code"
            },
            {
                "title": "海尔编码",
                "data": "hrCode",
                "name": "hrCode"
            },
            {
                "title": "名称",
                "data": "name",
                "name": "name"
            }],
        "displayLength": 5
    });

    $("#J_AddProduct_Button").click(function () {
        layer.open({
            content: $('#J_AddProduct').html(),
            area: ['500px', 'auto'],
            btn: ['确认', '取消'],
            zIndex: 9999,
            success: function () {
                // $('#J_shipmentTime').flatpickr({
                //     maxDate: new Date(),
                //     locale: 'zh'
                // });
                // $('#J_deliverTime').flatpickr({
                //     minDate: new Date(),
                //     locale: 'zh'
                // });
            },
            yes: function (index, layero) {
                var value = getValue(layero);
                if (value) {
                    $.ajax('addProduct', {
                        method: 'post',
                        data: value,
                        success: function () {
                            layer.close(index);
                            location.reload();
                        }
                    });
                }
            }
        });
    });
});