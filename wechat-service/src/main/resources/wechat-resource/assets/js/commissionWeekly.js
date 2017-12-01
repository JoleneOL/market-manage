/**
 * Created by Neo on 2017/7/14.
 */
$(function () {

    var extraHeight = 0;
    var commItems = $('.js-commItems');
    $('.js-extra-h-c').each(function () {
        extraHeight += $(this).outerHeight(true);
    });

    commItems.height($(window).height() - Math.ceil(extraHeight));

    var commTpl = function (obj) {
        return '<div class="view-comm-list_item"> ' +
            '<div class="weui-flex"> ' +
            '<div class="weui-flex__item">' + obj.commType + '</div> ' +
            '<div class="weui-flex__item">' + obj.name + '</div> ' +
            '<div class="weui-flex__item"><strong>￥' + obj.commission + '</strong></div> ' +
            '</div> ' +
            '<div class="weui-flex"> ' +
            '<div class="weui-flex__item">(' + obj.divided + '分成）</div> ' +
            '<div class="weui-flex__item">' + obj.commInfo + '</div> ' +
            '<div class="weui-flex__item text-gray">' + obj.commTime + '</div> ' +
            '</div> ' +
            '</div>';
    };

    commItems.each(function () {
        var self = $(this);
        self.myScroll({
            ajaxUrl: self.attr('data-url'),
            template: commTpl
        });
    });
});