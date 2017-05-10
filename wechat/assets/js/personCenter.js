/**
 * Created by Neo on 2017/5/10.
 */
$(function () {
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


    $('#equipment').infinite().on("infinite", function() {
        var self = $(this);
        if(self.attr('data-loading') === "1") return;
        self.attr('data-loading', 1);
        getEquipment(self);
    });

    function getEquipment($ele) {
        $.ajax('/api/teamList', {
            method: 'GET',
            data: {
                length: 10
            },
            dataType: 'json',
            success: function (res) {
                if (res.resultCode !== 200) {
                    $.toast('请求失败', 'cancel');
                    $ele.attr('data-loading', 0);
                    return false;
                }
                if(res.data.length > 0) {
                    // $('.weui-loadmore').before(createDom(res.data));
                    $ele.attr('data-loading', 0);
                } else {
                    // $('.weui-loadmore').text('没有更多内容了');
                    // $('#J_teamList').destroyInfinite();
                    $ele.attr('data-loading', 1);
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
        return '<div class="weui-flex">' +
            '<div class="weui-flex__item">' + obj.name + '</div>' +
            '<div class="weui-flex__item">' + obj.rank + '</div>' +
            '<div class="weui-flex__item view_flex-2">' + obj.joinTime + '</div>' +
            '<div class="weui-flex__item view_flex-2">' + obj.phone + '</div>' +
            '</div>';
    }
});