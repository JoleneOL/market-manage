$(function () {

    function filterFunc() {
        var _tag = $('input[name="tag"]').val();
        var _property = $('input[name="propertyValue"]').val();
        var _price = $('input[name="price"]').val();

        var url = infiniteWrap.attr('data-url');
        $.showLoading();
        $.ajax(url, {
            method: "GET",
            data: {
                tag: _tag,
                propertyValue: _property,
                price: _price,
                page: 0
            },
            dataType: 'json',
            success: function (res) {
                console.log(res);
                if (res.resultCode !== 200) {
                    return $.toptip(res.resultMsg);
                }
                setGoodsList(res.data);
                $.hideLoading();
                myScroll.reset({
                    ajaxData: {
                        tag: _tag,
                        propertyValue: _property,
                        price: _price
                    },
                    page: 1
                });
                $.myScrollRefresh(true);
            },
            error: function () {
                $.toast('服务器异常', 'cancel');
            }
        });
    }

    function setGoodsList(obj) {
        var domStr = '';
        if (obj.length > 0) {
            obj.forEach(function (v) {
                domStr += goodsTpl(v);
            });
            infiniteWrap
                .find('.search-result_item').remove()
                .end()
                .find('.weui-loadmore').html('<i class="weui-loading"></i><span class="weui-loadmore__tips">数据加载中...</span>').before(domStr);
        } else {
            infiniteWrap
                .find('.search-result_item').remove()
                .end()
                .find('.weui-loadmore').html('没有更多内容了');
        }
    }

    var infiniteWrap = $('.view-scroll-wrap');

    var goodsTpl = function (obj) {
        var detailUrl = $('body').attr('data-detail-url');
        if(detailUrl.indexOf('goodsDetail.html') <= -1 ){
            detailUrl += obj.id;
        }
        return '<a href="' +  detailUrl + '" class="search-result_item" goods-id="' + obj.id + '">' +
            '    <img src="' + obj.goodsImage + '">' +
            '    <div class="search-result_item-bd">' +
            '        <h4 class="goods-name">' + obj.productName + '</h4>' +
            '        <p class="goods-describe">' + obj.description + '</p>' +
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
            propertyValue: $('input[name="propertyValue"]').val(),
            price: $('input[name="price"]').val()
        },
        template: goodsTpl
    });


    $('.filter-tag').click(function () {
        $('.js-tagDrop').show()
            .siblings().hide()
            .closest('.drop-down').show();
    });

    $('.filter-prop').click(function () {
        $('.js-propDrop').show()
            .siblings().hide()
            .closest('.drop-down').show();
    });

    $('.filter-sort-price').click(function () {
        $('.drop-down-wrap').hide().closest('.drop-down').hide();
        $(this).toggleClass('arrow-down').toggleClass('arrow-up');
        if ($(this).hasClass('arrow-down')) {
            $('input[name="price"]').val('asc');
        } else {
            $('input[name="price"]').val('desc');
        }
        filterFunc();
    });

    $('.js-tagDrop').find('li').click(function () {
        $(this).addClass('active').siblings().removeClass('active');
        $(this).closest('.drop-down').hide();
        $('.filter-tag').find('span').text($(this).text());
        $('input[name="tag"]').val($(this).data('id'));
        filterFunc();
    });

    $('.js-propDrop').find('li').click(function () {
        $(this).addClass('active').siblings().removeClass('active');
        $(this).closest('.drop-down').hide();
        $('.filter-prop').find('span').text($(this).text());
        $('input[name="propertyValue"]').val($(this).data('id'));
        filterFunc();
    });
});