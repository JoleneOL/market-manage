/**
 * Created by Neo on 2017/5/10.
 */
$(function () {

    var orderDetailURL = 'orderDetail.html';
    var logisticsDetailURL = 'logisticsDetail.html';

    $('#J_orderList').myScroll({
        ajaxUrl: '/api/orderList',
        template: function (obj) {
            return '<div class="weui-form-preview view-mb-20 view-form-card_line">' +
                '<div class="view-form-preview-ex"> <div class="weui-form-preview__item">' +
                '<p class="weui-form-preview__value">订单时间：' + obj.orderTime + '</p>' +
                '<label class="weui-form-preview__label text-black">' + obj.orderStatus + '</label> ' +
                '</div> ' +
                '</div> ' +
                '<div class="weui-form-preview__bd">' +
                '<div class="weui-flex"> ' +
                '<div class="weui-flex__item">' + obj.name + '</div>' +
                '<div class="weui-flex__item">' + obj.goodsInfo + '</div>' +
                '<div class="weui-flex__item">￥' + obj.orderAmount + '</div>' +
                '</div> ' +
                '<div class="weui-flex">' +
                '<div class="weui-flex__item">' + obj.phone + '</div>' +
                '<div class="weui-flex__item">' + obj.package + '</div>' +
                '<div class="weui-flex__item">x' + obj.quantity + '</div> ' +
                '</div>' +
                '</div>' +
                '<div class="weui-form-preview__ft view_form-button-group">' +
                '<div class="button_sp_area">' +
                '<a href="' + orderDetailURL + '?orderId=' + obj.orderId + '" class="weui-btn weui-btn_mini weui-btn_default_custom">详情</a>' +
                '<a href="' + logisticsDetailURL + '?orderId=' + obj.orderId + '" class="weui-btn weui-btn_mini weui-btn_default_custom">查看物流</a>' +
                '</div>' +
                '</div>' +
                '</div>';
        }
    });


});