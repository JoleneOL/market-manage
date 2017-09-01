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
        var link = '',
            more = '';
        if (obj.nextRank && obj.id) {
            link = $('body').attr('data-memberList-url-template').replace('{ID}', obj.id).replace('{RANK}', obj.nextRank);
            more = '<div class="weui-cell__ft"></div>';
        } else {
            link = 'javascript:;';
        }

        return '<div class="weui-cells view-mt-0">\n' +
            '    <a class="weui-cell weui-cell_access view-team-list_link" href="' + link + '">\n' +
            '        <div class="weui-cell__bd view-team-list_items">\n' +
            '            <div class="ellipsis">' + obj.name + '</div>\n' +
            '            <div class="ellipsis">' + obj.rank + '</div>\n' +
            '            <div class="ellipsis">' + obj.joinTime + '</div>\n' +
            '            <div class="ellipsis">' + obj.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') + '</div>\n' +
            '        </div>\n' +
            more +
            '    </a>\n' +
            '</div>'
    };

    $('.js-teamItems').each(function () {
        var self = $(this);
        self.myScroll({
            ajaxUrl: self.attr('data-url'),
            template: listTpl
        });
    })

});