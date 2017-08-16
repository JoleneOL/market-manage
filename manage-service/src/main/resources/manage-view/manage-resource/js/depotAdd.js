/**
 * Created by Neo on 2017/6/30.
 */
$(function () {

    $.validator.addMethod("hasCity", function (value, element) {
        var val = $('#J_cityPicker').val();
        return val.split(' ').length === 3;
    }, "请选择完整的地址");

    $('#J_depotForm').validate({
        rules: {
            name: "required",
            fullAddress: {
                required: true,
                hasCity: true
            }
        },
        messages: {
            name: "请填写仓库名称",
            fullAddress: {
                required: '请填写详细地址'
            },
            haierCode: "请填写仓库编号"
        },
        errorElement: "span",
        errorPlacement: function (error, element) {
            error.addClass("help-block");
            if (element.prop("type") === "checkbox") {
                element.siblings('label').addClass('error');
            } else {
                error.insertAfter(element);
            }
        },
        highlight: function (element, errorClass, validClass) {
            $(element).parent().addClass("has-error").removeClass("has-success");
        },
        unhighlight: function (element, errorClass, validClass) {
            if ($(element).prop("type") === "checkbox") {
                $(element).siblings('label').removeClass('error');
            } else {
                $(element).parent().addClass("has-success").removeClass("has-error");
            }
        },
        submitHandler: function (form) {
            form.submit();
        }
    });
});