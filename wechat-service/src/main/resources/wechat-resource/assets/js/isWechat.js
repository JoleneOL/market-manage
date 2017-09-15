$(function () {
    var $mask = $('.mask');
    function isWechat() {
        var ua = navigator.userAgent.toLowerCase();
        return ua.match(/MicroMessenger/i) == "micromessenger";
    }
    if(isWechat()) $mask.show();

    $mask.click(function () {
        $(this).hide();
    })
});