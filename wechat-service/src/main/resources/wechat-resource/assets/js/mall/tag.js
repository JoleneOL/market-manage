$(function () {

    $('#J_filter').click(function () {
        var _date = $('input[name="tag"]').val();
        var _remark = $('input[name="property"]').val();
        var _deal = $('input[name="price"]').val();

        var url = infiniteWrap.attr('data-url');
        $.showLoading();
        $.ajax(url, {
            method: "GET",
            data: {
                date: _date,
                remark: _remark,
                deal: _deal,
                page: 0
            },
            dataType: 'json',
            success: function (res) {
                if (res.resultCode !== 200) {
                    $.toast('请求失败', 'cancel');
                    return '';
                }
                setGoodsList(res.data);
                $.hideLoading();
                myScroll.reset({
                    ajaxData: {
                        date: _date,
                        remark: _remark,
                        deal: _deal
                    },
                    page: 1
                });
                $.myScrollRefresh(true);
            },
            error: function () {
                $.toast('服务器异常', 'cancel');
            }
        });

    });

    function setGoodsList(obj) {
        var domStr = '';
        if (obj.length > 0) {
            obj.forEach(function (v) {
                domStr += salesTpl(v);
            });
            infiniteWrap
                .find('.view-sales-item').remove()
                .end()
                .find('.weui-loadmore').before(domStr);
        } else {
            infiniteWrap
                .find('.view-sales-item').remove()
                .end()
                .find('.weui-loadmore').text('没有更多内容了');
        }
    }

    var infiniteWrap = $('.view-scroll-wrap');

    var goodsTpl = function (obj) {
        return '<a href="javascript:;" class="search-result_item">' +
            '    <img src="' + obj.goodsImage + '">' +
            '    <div class="search-result_item-bd">' +
            '        <h4 class="goods-name">' + obj.productName + '</h4>' +
            '        <p class="goods-describe">' + obj.tags + '</p>' +
            '        <div class="goods-info">' +
            '            <div class="goods-price">￥' + obj.price + '</div>' +
            '        </div>' +
            '    </div>' +
            '</a>';
    };

    var extraHeight_team = 0;
    $('.js-extra-h').each(function () {
        extraHeight_team += $(this).outerHeight(true);
    });

    infiniteWrap.height($(window).height() - Math.ceil(extraHeight_team) - 30);

    var myScroll = infiniteWrap.myScroll({
        debug: true,
        ajaxUrl: infiniteWrap.attr('data-url'),
        ajaxData: {
            tag: $('input[name="tag"]').val(),
            property: $('input[name="property"]').val(),
            price: $('input[name="price"]').val()
        },
        template: goodsTpl
    });

});