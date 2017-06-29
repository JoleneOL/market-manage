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


    var navbarItem = $('.view-navbar_item');
    var navbarSwiper = $('#navbar-container').swiper({
        observer: true,
        observeParents: true,
        speed: 500,
        onSlideChangeStart: function () {
            $(".weui-navbar .on").removeClass('on');
            navbarItem.eq(navbarSwiper.activeIndex).addClass('on');
        }
    });
    navbarItem.on('touchstart mousedown', function (e) {
        e.preventDefault();
        $(".weui-navbar .on").removeClass('on');
        $(this).addClass('on');
        navbarSwiper.slideTo($(this).index())
    });
    navbarItem.click(function (e) {
        e.preventDefault();
    });

    var extraHeight = 0;
    $('.js-extra-h-c').each(function () {
        extraHeight += $(this).outerHeight(true);
    });
    $('.swiper-slide').height($(window).height() - Math.ceil(extraHeight) - 52);

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

    $('.js-commItems').each(function () {
        var self = $(this);
        self.myScroll({
            ajaxUrl: self.attr('data-url'),
            template: commTpl
        });
    });


    // 我的团队逻辑
    var infiniteWrap = $('#J_teamList');

    var extraHeight_team = 0;
    $('.js-extra-h').each(function () {
        extraHeight_team += $(this).outerHeight(true);
    });

    infiniteWrap.height($(window).height() - Math.ceil(extraHeight_team));

    var listTpl = function (obj) {
        return '<div class="view-list-item">' +
            '<div class="ellipsis">' + obj.name + '</div>' +
            '<div>' + obj.phone + '</div>' +
            '<div>' + obj.rank + '</div>' +
            '<div>' + obj.joinTime + '</div>' +
            '</div>';
    };

    var myScroll = infiniteWrap.myScroll({
        ajaxUrl: infiniteWrap.attr('data-url'),
        ajaxData: {
            rank: infiniteWrap.attr('data-rank')
        },
        template: listTpl
    });

    $('#J_changeRank').click(function () {
        $.actions({
            title: "选择级别",
            actions: [
                {
                    text: "全部",
                    className: "text-custom",
                    onClick: function () {
                        $.showLoading();
                        getRankList('all');
                    }
                },
                {
                    text: "总代理",
                    onClick: function () {
                        $.showLoading();
                        getRankList(1);
                    }
                },
                {
                    text: "代理商",
                    onClick: function () {
                        $.showLoading();
                        getRankList(2);
                    }
                },
                {
                    text: "经销商",
                    onClick: function () {
                        $.showLoading();
                        getRankList(3);
                    }
                },
                {
                    text: "爱心天使",
                    onClick: function () {
                        $.showLoading();
                        getRankList(4);
                    }
                }
            ]
        });
    });


    function getRankList(data) {
        var url = infiniteWrap.attr('data-url');
        $.ajax(url, {
            method: "GET",
            data: {rank: data, page: 0},
            dataType: 'json',
            success: function (res) {
                if (res.resultCode !== 200) {
                    $.toast('请求失败', 'cancel');
                    return '';
                }
                setRankList(res.data);
                $.hideLoading();
                infiniteWrap.attr('data-rank', data);
                myScroll.reset({
                    ajaxData: {
                        rank: infiniteWrap.attr('data-rank')
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

    function setRankList(obj) {
        var domStr = '';
        if (obj.length > 0) {
            obj.forEach(function (v) {
                domStr += listTpl(v);
            });
        } else {
            domStr = '<div class="view-list-item view-no-res"><p class="text-center">暂无数据</p></div>'
        }
        infiniteWrap
            .find('.view-list-item').remove()
            .end()
            .find('.weui-loadmore').before(domStr);
    }
});