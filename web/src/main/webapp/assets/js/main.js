/**
 * Created by Chang on 2017/4/25.
 */
$(function () {
    "use strict";

    // 返回按钮方法
    $('.js-goBack').click(function () {
        window.history.back();
    });
    // 面包屑导航返回
    $('.pageheader').find('.breadcrumb').find('a').click(function () {
        window.history.back();
    });

    // 初始化 datepicker
    if( $('#datepicker').length > 0) {
        $('#datepicker').datepicker({
            format: 'yyyy-mm-dd',
            language: 'zh-CN',
            endDate : new Date()
        });
    }

    $('.js-navActive').find('li').not('.divider').click(function () {
        $(this).addClass('active').siblings().removeClass('active');
    });

    // Menu Toggle
    $('.menutoggle').click(function () {

        var body = jQuery('body');
        var bodypos = body.css('position');

        if (bodypos !== 'relative') {

            if (!body.hasClass('leftpanel-collapsed')) {
                body.addClass('leftpanel-collapsed');
                $('.nav-bracket ul').attr('style', '');

                $(this).addClass('menu-collapsed');

            } else {
                body.removeClass('leftpanel-collapsed chat-view');
                $('.nav-bracket li.active ul').css({display: 'block'});

                $(this).removeClass('menu-collapsed');

            }
        } else {

            if (body.hasClass('leftpanel-show'))
                body.removeClass('leftpanel-show');
            else
                body.addClass('leftpanel-show');

        }
    });

    // Minimize Button in Panels
    $('.minimize').click(function () {

        var t = $(this);
        var p = t.closest('.panel');
        if (!$(this).hasClass('maximize')) {
            p.find('.panel-body, .panel-footer').slideUp(200);
            t.addClass('maximize');
            t.html('&plus;');
        } else {
            p.find('.panel-body, .panel-footer').slideDown(200);
            t.removeClass('maximize');
            t.html('&minus;');
        }
        return false;
    });

});