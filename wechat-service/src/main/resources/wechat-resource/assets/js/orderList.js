/**
 * Created by Neo on 2017/5/10.
 */
$(function () {

    var $body = $('body');
    var orderDetailURL = $body.attr('data-detail-url');
    var payURL = $body.attr('data-pay-url');
    var logisticsDetailURL = 'logisticsDetail.html';
    var orderTpl = function (obj) {
        var toPay = obj.statusCode == 1 ? '<a href="' + payURL + '?orderId=' + obj.orderId + '" class="weui-btn weui-btn_mini weui-btn_default_custom">支付</a>' : '';
        return '<div class="weui-form-preview view-mb-20 view-form-card_line">' +
            '<div class="view-form-preview-ex"> <div class="weui-form-preview__item">' +
            '<p class="weui-form-preview__value">订单时间：' + obj.orderTime + '</p>' +
            '<label class="weui-form-preview__label text-black">' + obj.status + '</label> ' +
            '</div> ' +
            '</div> ' +
            '<div class="weui-form-preview__bd">' +
            '<div class="weui-flex"> ' +
            '<div class="weui-flex__item">' + obj.orderUser + '</div>' +
            '<div class="weui-flex__item">' + obj.category + ' ' + obj.type + '</div>' +
            '<div class="weui-flex__item">￥' + obj.total + '</div>' +
            '</div> ' +
            '<div class="weui-flex">' +
            '<div class="weui-flex__item">' + obj.phone + '</div>' +
            '<div class="weui-flex__item">' + obj.package + '</div>' +
            '<div class="weui-flex__item">x' + obj.amount + '</div> ' +
            '</div>' +
            '</div>' +
            '<div class="weui-form-preview__ft view_form-button-group">' +
            '<div class="button_sp_area">' +
            '<a href="' + orderDetailURL + '?orderId=' + obj.orderId + '" class="weui-btn weui-btn_mini weui-btn_default_custom">详情</a>' +
            // '<a href="' + logisticsDetailURL + '?orderId=' + obj.orderId + '" class="weui-btn weui-btn_mini weui-btn_default_custom">查看物流</a>' +
            toPay +
            '</div>' +
            '</div>' +
            '</div>';
    };

    // $('#J_orderList').myScroll({
    //     ajaxUrl: '/api/orderList',
    //     template: orderTpl
    // });

    var tabsItem = $('.view-tabs_item');
    var tabsSwiper = $('#tabs-container').swiper({
        observer: true,
        observeParents: true,
        speed: 500,
        onSlideChangeStart: function () {
            $(".view-tabs .active").removeClass('active');
            tabsItem.eq(tabsSwiper.activeIndex).addClass('active');
        }
    });
    tabsItem.on('touchstart mousedown', function (e) {
        e.preventDefault();
        $(".view-tabs .active").removeClass('active');
        $(this).addClass('active');
        tabsSwiper.slideTo($(this).index())
    });
    tabsItem.click(function (e) {
        e.preventDefault();
    });

    var extraHeight = 0;
    $('.js-extra-h-c').each(function () {
        extraHeight += $(this).outerHeight(true);
    });
    $('.swiper-slide').height($(window).height() - Math.ceil(extraHeight) - 52);

    $('.js-commItems').each(function () {
        var self = $(this);
        self.myScroll({
            ajaxUrl: self.attr('data-url'),
            template: orderTpl
        });
    });

});