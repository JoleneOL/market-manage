/**
 * Created by Neo on 2017/6/8.
 */
$(function () {
    $('#J_invoiceAddress').cityPicker({
        title: "请选择收货地址",
        onChange: function () {
            $('#J_cityPicker').closest('.weui-cell').removeClass('weui-cell_warn');
        }
    });
    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1([34578])\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");

    $('#J_invoiceForm').validate({
        rules: {
            company: "required",
            invoiceAddressee: 'required',
            invoiceAddress: 'required',
            invoiceFullAddress: "required",
            invoiceMobile: {
                required: true,
                isPhone: true
            }
        },
        messages: {
            company: "请填写公司抬头",
            invoiceAddressee: "请填写公司抬头",
            invoiceAddress: "请填写收件地址",
            invoiceFullAddress: "请填写详细地址",
            invoiceMobile: {
                required: "请填写联系电话"
            }
        },
        errorPlacement: function (error, element) {
            $.toptip(error, 1000);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').addClass("weui-cell_warn")
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').removeClass("weui-cell_warn");
        },
        submitHandler: function (form) {
            form.submit();
        }
    });
});