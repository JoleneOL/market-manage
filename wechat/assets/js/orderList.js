/**
 * Created by Neo on 2017/5/10.
 */
$(function () {

    // 阻止多次请求
    var loading = false;

    var orderDetailURL = 'orderDetail.html';
    var logisticsDetailURL = 'logisticsDetail.html';

    $(document.body).infinite().on("infinite", function () {
        if (loading) return;
        loading = true;
        getDate();
    });


    function getDate() {
        $.ajax('/api/orderList', {
            method: 'GET',
            data: {
                length: 10
            },
            dataType: 'json',
            success: function (res) {
                if (res.resultCode !== 200) {
                    $.toast('请求失败', 'cancel');
                    loading = false;
                    return false;
                }
                if (res.data.length > 0) {
                    $('.weui-loadmore').before(createDom(res.data));
                    loading = false;
                } else {
                    $('.weui-loadmore').text('没有更多内容了');
                    $(document.body).destroyInfinite();
                    loading = true;
                }
            },
            error: function () {
                $.toast('服务器异常', 'cancel');
            }
        });
    }


    function createDom(obj) {
        var domStr = '';
        obj.forEach(function (v) {
            domStr += templateStr(v);
        });
        return domStr;
    }


    function templateStr(obj) {
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
            '</div> <div class="weui-flex">' +
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