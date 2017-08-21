$(function () {

    if (window.document.location.search.indexOf('type=codeError') >= 0) {
        $.toptip('验证码错误');
    }

    var sendAuthCodeUrl = $('body').attr('data-url-sendAuthCode');

    $('#J_authCode').click(function () {
        var self = $(this);
        sendSMS(self);
        $.ajax(sendAuthCodeUrl, {
            method: 'POST',
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

    $('#J_authCode').trigger('click');
});