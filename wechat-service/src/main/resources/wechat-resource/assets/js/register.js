/**
 * Created by Chang on 2017/7/5.
 */
$(function () {
    "use strict";

    if (window.document.location.search.indexOf('type=codeError') >= 0) {
        $.toptip('验证码错误');
    }

    //TODO 是否需要验证手机号是否注册

    $('#J_registerForm').validate({
        rules: {
            name: "required",
            mobile: "required",
            authCode: "required"
        },
        messages: {
            username: "请填写姓名",
            mobile: "请填写手机号",
            authCode: "请填写验证码"
        },
        errorPlacement: function (error, element) {
            $.toptip(error);
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

    var $mobile = $('#J_mobile');
    var sendAuthCodeUrl = $('body').attr('data-url-sendAuthCode');

    $('#J_authCode').click(function () {
        var self = $(this);
        var mobile = $mobile.val();
        if (!/^1([34578])\d{9}$/.test(mobile)) {
            $.toptip('请输入正确的手机号');
            return;
        }
        sendSMS(self);
        $.ajax(sendAuthCodeUrl, {
            method: 'POST',
            data: {
                mobile: mobile
            },
            dataType: 'json',
            success: function (data) {
                if (data.resultCode === 401) {
                    $.toptip(data.resultMsg);
                    return false;
                }
                if (data.resultCode !== 200) {
                    $.toptip("发送失败，请重试");
                    return false;
                }
            },
            error: function () {
                $.toptip("系统错误");
            }
        })
    });

    function sendSMS(ele) {
        ele.prop('disabled', true);
        var s = 60;
        var t = setInterval(function () {
            ele.text(s-- + 's');
            if (s === -1) {
                clearInterval(t);
                ele.text('获取验证码')
                    .prop('disabled', false);
            }
        }, 1000);
    }
});