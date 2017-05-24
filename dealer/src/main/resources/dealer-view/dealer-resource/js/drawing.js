/**
 * Created by Chang on 2017/5/1.
 */
$(function () {
    "use strict";

    var $invoice = $('#J_haveInvoice');
    $('.js-invoice').change(function () {
       if($(this).val() === '1') {
           $invoice.slideDown(200);
           $('input[name="logisticsCode"]').rules('add', {
               required: true,
               messages : {
                   required : "填写物流单号"
               }
           });
           $('input[name="logisticsCompany"]').rules('add', {
               required: true,
               messages : {
                   required : "填写物流公司"
               }
           });
       } else {
           $invoice.slideUp(200);
           $('input[name="logisticsCode"]').rules('remove');
           $('input[name="logisticsCompany"]').rules('remove');
       }
    });
    var amount = $('#J_withdrawAmount');
    var withdraw = $('#J_withdraw');

    $('#J_Bank').on('keyup mouseout input', function () {
        var $this = $(this);
        var v = $this.val();
        /\S{5}/.test(v) && $this.val(v.replace(/\s/g, '').replace(/(.{4})/g, "$1 "));
        $('input[name="account"]').val($this.val().replace(/\s/g, ''));
    });

    withdraw.on('keyup mouseout input', function () {
        var text = $(this).val() ? $(this).val() : 0;
        amount.text(text);
    });

    // 粗略的手机号正则
    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1(3|4|5|7|8)\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");

    // 整正数
    $.validator.addMethod("isPositive", function (value, element) {
        var score = /^[0-9]*$/;
        return this.optional(element) || (score.test(value));
    }, "请输入整正数");
    // 最多两位小数
    $.validator.addMethod("isFloat2", function (value, element) {
        var score = /^[0-9]+\.?[0-9]{0,2}$/;
        return this.optional(element) || (score.test(value));
    }, "最多可输入两位小数");

    $.validator.setDefaults({
        submitHandler: function (form) {
            form.submit();
        }
    });

    $('#J_form').validate({
        ignore: "",
        rules: {
            payee: "required",
            account: {
                required: true,
                number: true,
                isPositive: true
            },
            bank: 'required',
            mobile: {
                required: true,
                isPhone: true
            },
            withdraw: {
                required: true,
                number: true,
                min: 0.01,
                isFloat2: true
            }
        },
        messages: {
            payee: "请填写收款人",
            account: {
                required: "请填写收款账号",
                number: "请输入正确的收款账号",
                digits: "请输入正确的收款账号"
            },
            bank: "请填写开户行",
            mobile: {
                required: "请填写手机号码"
            },
            withdraw: {
                required: "请填写提款金额",
                number: "请填写正确金额",
                min: "提款最小金额为 {0}",
                max: "提款最大金额为 {0}"
            }
        },
        errorElement: "span",
        errorPlacement: function (error, element) {
            error.addClass("help-block");
            if (element.prop("type") === "checkbox") {
                element.siblings('label').addClass('error');
            } else {
                element.parent().append(error);
            }
        },
        highlight: function (element, errorClass, validClass) {
            $(element).parent().addClass("has-error").removeClass("has-success");
        },
        unhighlight: function (element, errorClass, validClass) {
            if ($(element).prop("type") === "checkbox") {
                $(element).siblings('label').removeClass('error');
            } else {
                $(element).parent()
                    .addClass("has-success").removeClass("has-error")
                    .find('.help-block').hide();
            }

        }
    });


    $('#J_agreement').click(function () {
        window.top.layer.open({
            type: 1,
            shade: 0.5,
            title: false,
            area: ['960px', '500px'],
            content: $('#J_protocol').html()
        });
    });

});
