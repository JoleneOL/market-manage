/**
 * Created by Chang on 2017/5/7.
 */
$(function () {
    $('#J_upgradeRules').click(function () {
        $.toast("满30个人即可升级", "text");
    });

    $(document.body).infinite().on("infinite", function() {
        setTimeout(function() {
            $(".view_team-list").append("<p> 我是新加载的内容 </p>");
        }, 1500);   //模拟延迟
    });
});