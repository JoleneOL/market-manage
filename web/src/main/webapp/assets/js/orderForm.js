/**
 * Created by Chang on 2017/5/2.
 */
$(function () {
    // 粗略的手机号正则
    jQuery.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1(3|4|5|7|8)\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");
    // 曲线救国验证 地址是否选择
    jQuery.validator.addMethod("hasCity", function (value, element) {
        var val = $('#J_cityPicker').val();
        return val.split("/").length === 3;
    }, "请选择完整的地址");

    $('#J_isAgree').click(function () {
        window.top.layer.open({
            type: 1,
            shade: 0.5,
            title: false,
            area: ['960px', '500px'],
            content: $('#J_protocol').html()
        });
    });
    $.validator.setDefaults({
        submitHandler: function (form) {
            form.submit();
        }
    });
    $('#J_orderPlace').validate({
        rules: {
            customer: "required",
            age: {
                required: true,
                number: true,
                digits: true
            },
            fullAddress: {
                required: true,
                hasCity: true
            },
            phone: {
                required: true,
                isPhone: true
            },
            amount: {
                required: true
            },
            isAgree: "required"
        },
        messages: {
            customer: "请填写客户姓名",
            age: {
                required: "请填写年龄",
                digits: "请输入整数"
            },
            amount: {
                required: "请填写购买数量"
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

        }
    });
});