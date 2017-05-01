/**
 * Created by Chang on 2017/4/25.
 */
$(function () {
    "use strict";

    // 初始化 datepicker
    if ($('#datepicker').length > 0) {
        $('#datepicker').datepicker({
            format: 'yyyy-mm-dd',
            language: 'zh-CN',
            clearBtn: true,
            endDate: new Date()
        });
    }

    // 导航切换效果
    $('.js-navActive').find('li').not('.divider').click(function () {
        $(this).addClass('active').siblings().removeClass('active');
    });

    // 侧栏菜单切换
    $('.menutoggle').click(function () {
        var body = $('body');
        body.toggleClass('leftpanel-collapsed');
        $(this).toggleClass('menu-collapsed');
    });

    // 面板打开关闭
    $('.minimize').click(function () {
        var self = $(this);
        var parent = self.closest('.panel');
        parent.find('.panel-body, .panel-footer').slideToggle(200);
        self.find('i').toggleClass('fa-minus');
        return false;
    });

});