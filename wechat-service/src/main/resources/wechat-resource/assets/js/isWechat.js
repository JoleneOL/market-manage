$(function () {
    function isWechat() {
        var ua = navigator.userAgent.toLowerCase();
        return ua.match(/MicroMessenger/i) == "micromessenger";
    }
    if(isWechat()) $('.mask').show();
});