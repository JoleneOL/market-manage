/**
 * Created by Neo on 2017/5/19.
 */
$(function () {
    "use strict";

    if (window.document.location.search.indexOf('type=error') >= 0) {
        $.toptip('用户名或者密码错误');
    }
    if (window.document.location.search.indexOf('type=codeError') >= 0) {
        $.toptip('用户名或者验证码错误');
    }
    if (window.document.location.search.indexOf('type=typeError') >= 0) {
        $.toptip('该用户无法在此登录');
    }

    $.validator.setDefaults({
        submitHandler: function (form) {
            form.submit();
        }
    });

    $('#J_passwordForm').validate({
        rules: {
            username: "required",
            password: "required"
        },
        messages: {
            username: "请填写账户",
            password: "请填写密码"
        },
        errorPlacement: function (error, element) {
            $.toptip(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').addClass("weui-cell_warn")
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').removeClass("weui-cell_warn");
        }
    });

    $('#J_messageForm').validate({
        rules: {
            mobile: "required",
            authCode: "required"
        },
        messages: {
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
                if (data.resultCode == 400) {
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
        var s = 30;
        var t = setInterval(function () {
            ele.text(s-- + '秒');
            if (s === -1) {
                clearInterval(t);
                ele.text('获取验证码')
                    .prop('disabled', false);
            }
        }, 1000);
    }
});