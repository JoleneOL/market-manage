/**
 * Created by Neo on 2017/7/10.
 */
$(function () {

    $(".i-checks").iCheck({
        checkboxClass: "icheckbox_square-green",
        radioClass: "iradio_square-green"
    });

    var form = $('#J_userForm');
    var dataId = form.attr('data-id');
    if (dataId) {
        form.attr('action', form.attr('data-edit-action'));
    }

    form.validate({
        rules: {
            name: "required",
            department: "required",
            "role": {
                required: true,
                minlength: 1
            }
        },
        messages: {
            name: "请填写用户名"
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