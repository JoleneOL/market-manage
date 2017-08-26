/**
 * Created by Neo on 2017/5/10.
 */
$(function () {

    var $body = $('body');
    var orderDetailURL = $body.attr('data-detail-url');
    var payURL = $body.attr('data-pay-url');
    var logisticsDetailURL = 'logisticsDetail.html';
    var invoiceURL = $body.attr('data-invoice-url');
    var orderTpl = function (obj) {
        return '<div class="view-order-list view-mb-20">\n' +
            '        <div class="weui-cells__title">\n' +
            '            <p class="weui-cell__bd text-black">收件人：' + obj.orderUser + '<br>手机号：' + obj.phone + '</p>\n' +
            '            <label class="text-black">' + orderList.statusString(obj)  + '</label>' +
            '        </div>\n' +
            '        <div class="weui-cells">\n' +
            goodsList(obj) +
            '        </div>\n' +
            '        <div class="view-order-list_ex">\n' +
            '            <p class="view-order_ex_title">合计：￥' + obj.total + '</p>\n' +
            '            <p class="view-order_ex_title text-gray">订单时间：' + obj.orderTime + '</p>\n' +
            '        </div>\n' +
            '        <div class="view-order-list_ft">\n' +
            '            <div class="button_sp_area">\n' +
            // orderList.hasInvoice(obj) +
            orderList.toPay(obj) +
            '                <a href="' + orderDetailURL + '?orderId=' + obj.orderId + '" class="weui-btn weui-btn_mini weui-btn_default_custom">详情</a>' +
            // '             <a href="' + logisticsDetailURL + '?orderId=' + obj.orderId + '" class="weui-btn weui-btn_mini weui-btn_default_custom">查看物流</a>' +
            '            </div>\n' +
            '        </div>\n' +
            '    </div>'
    };


    var orderList = {
        toPay: function (obj) {
            var dom = '';
            if (obj.statusCode === 1) {
                var a = '<a href="' + payURL + '?orderId=' + obj.orderId + '" class="weui-btn weui-btn_mini weui-btn_default_custom">取消订单</a>';
                var b = '<a href="' + payURL + '?orderId=' + obj.orderId + '" class="weui-btn weui-btn_mini weui-btn_default_primary">支付</a>';
                // return a + b;
                return b;
            }
            return dom;
        },
        hasInvoice: function (obj) {
            if (obj.hasInvoice) {
                return '<a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_default_disabled">已开票</a>'
            }
            //TODO 退款有两种情况，故有A B 两个页面留意
            return '<a href="refund-A.html" class="weui-btn weui-btn_mini weui-btn_default_custom">退款</a>' +
                '<a href="' + invoiceURL + '?orderId=' + obj.orderId + '" class="weui-btn weui-btn_mini weui-btn_default_custom">开发票</a>'

        },
        statusString: function (obj) {
            if (obj.statusCode === 1) {
                return '未付款';
            } else {
                return '已付款';
            }

        }
    };

    var goodsList = function (obj) {
        var dom = '';
        $.each(obj.goods, function (i, v) {
            dom += '<div class="weui-cell">\n' +
                '       <div class="weui-cell__bd">' + v.name + '</div>\n' +
                '       <div class="weui-cell__ft">x' + v.amount + '</div>\n' +
                '   </div>\n';
        });
        return dom;
    };
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

    var myScrolls = [];
    $('.js-commItems').each(function () {
        var self = $(this);
        var myScroll = self.myScroll({
            ajaxUrl: self.attr('data-url'),
            template: orderTpl
        });
        myScrolls.push(myScroll)
    });

    $('#J_searchInput').on('keypress', function (e) {
        var keyCode = e.keyCode;
        var input = $(this).val();
        if (keyCode === 13) {
            e.preventDefault();
            $.each(myScrolls, function (i, v) {
                v.reload({
                    debug: true,
                    ajaxData: {search: input},
                    removeEle: '.view-order-list'
                })
            });
        }
    });
});