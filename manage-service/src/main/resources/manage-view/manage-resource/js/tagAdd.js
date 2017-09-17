/**
 * Created by Neo on 2017/6/30.
 */
$(function () {
    var dataUrl = $("#J_tagForm").attr('action');
    // 标签名称唯一性校验
    $.validator.addMethod("nameUnique", function (value, element) {
        if(this.optional(element))
            return false;
        $.ajax(dataUrl + '/' + $(this).attr('data-id') + "/check", {
            method: 'put',
            success: function (result) {
                return result == "true";
            }
        });
    }, "标签名称已存在");
    $('#J_tagForm').validate({
        rules: {
            name: {
                "required":true,
                "nameUnique":true,
            },
            J_Type: "required"
        },
        messages: {
            name: {
                "required":"请填写标签名称",
                "nameUnique":"标签名称已存在"
            },
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