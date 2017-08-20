/**
 * Created by Neo on 2017/6/30.
 */
$(function () {

    $('#J_cityPicker').on('cp:updated', function () {
        var val = $(this).val();
        $(this).val(val.replace(/\//g, ' '));
    });

    $.validator.addMethod("hasCity", function (value, element) {
        var val = $('#J_cityPicker').val();
        return val.split(' ').length === 3;
    }, "请选择完整的地址");

    $('#J_factoryForm').validate({
        rules: {
            name: "required",
            fullAddress: {
                required: true,
                hasCity: true
            }
        },
        messages: {
            name: "请填写工厂名称",
            fullAddress: {
                required: '请填写详细地址'
            }
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