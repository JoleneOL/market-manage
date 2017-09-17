/**
 * Created by Neo on 2017/6/30.
 */
$(function () {

    $('#J_tagForm').validate({
        rules: {
            name: "required",
            J_Type: "required"
        },
        messages: {
            name: "请填写标签名称",
            J_Type: "请选择标签类型"
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