/**
 * Created by Neo on 2017/7/14.
 */
$(function () {
    var teamItem = $('.js-team');
    var teamSwiper = $('#tabs-teams').swiper({
        observer: true,
        observeParents: true,
        speed: 500,
        onSlideChangeStart: function () {
            $(".js-teams .active").removeClass('active');
            teamItem.eq(teamSwiper.activeIndex).addClass('active');

            $('.view-total .on').removeClass('on');
            $('.view-total li').eq(teamSwiper.activeIndex).addClass('on');
        }
    });
    teamItem.on('touchstart mousedown', function (e) {
        e.preventDefault();
        $(".js-teams .active").removeClass('active');
        $(this).addClass('active');
        teamSwiper.slideTo($(this).index())
    });
    teamItem.click(function (e) {
        e.preventDefault();
    });

    // 我的团队逻辑
    var infiniteWrap = $('.swiper-slide');

    var extraHeight_team = 0;
    $('.js-extra-h').each(function () {
        extraHeight_team += $(this).outerHeight(true);
    });

    infiniteWrap.height($(window).height() - Math.ceil(extraHeight_team));

    var listTpl = function (obj) {
        return '<div class="weui-flex view_team-list">' +
            '<div class="weui-flex__item text-center">' + obj.name + '</div>' +
            '<div class="weui-flex__item text-center">' + obj.rank + '</div>' +
            '<div class="weui-flex__item text-center">' + obj.joinTime + '</div>' +
            '</div>';
    };

    $('.js-teamItems').each(function () {
        var self = $(this);
        self.myScroll({
            ajaxUrl: self.attr('data-url'),
            template: listTpl
        });
    })

});