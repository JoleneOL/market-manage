/**
 * 用于支付的JS
 * Created by CJ on 18/05/2017.
 */

var completeCheck = function (data) {
    // console.log(data);
    if (data && data == true) {
        $.success = true;//请勿注释
        window.location.href = $('body').attr('data-success-url');
    } else {
        //继续刷
        //等待一会儿
        // console.log('稍后再次检测');
        setTimeout('$._loginCheck()', 2000)
    }
};

$._loginCheck = function () {
    // console.log('检测是否已完成支付');
    $.ajax($('body').attr('data-check-url'), {
        method: 'get',
        dataType: 'json',
        success: completeCheck,
        error: function (msg) {
            // console.log(msg.responseText);
            setTimeout('$._loginCheck()', 5000);
        }
    });
};

setTimeout('$._loginCheck()', 2000);
