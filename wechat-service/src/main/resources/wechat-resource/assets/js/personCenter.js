/**
 * Created by Neo on 2017/5/10.
 */
$(function () {

    var tabsItem = $('.js-comm');
    var tabsSwiper = $('#tabs-container').swiper({
        observer: true,
        observeParents: true,
        speed: 500,
        onSlideChangeStart: function () {
            $(".js-comms .active").removeClass('active');
            tabsItem.eq(tabsSwiper.activeIndex).addClass('active');
        }
    });
    tabsItem.on('touchstart mousedown', function (e) {
        e.preventDefault();
        $(".js-comms .active").removeClass('active');
        $(this).addClass('active');
        tabsSwiper.slideTo($(this).index())
    });
    tabsItem.click(function (e) {
        e.preventDefault();
    });

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
    var commItems = $('.js-commItems');
    $('.js-extra-h-c').each(function () {
        extraHeight += $(this).outerHeight(true);
    });
    commItems.height($(window).height() - Math.ceil(extraHeight) - 52);

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

    commItems.each(function () {
        var self = $(this);
        self.myScroll({
            ajaxUrl: self.attr('data-url'),
            template: commTpl
        });
    });


    // 我的团队逻辑
    var teamItems = $('.js-teamItems');

    var extraHeight_team = 0;
    $('.js-extra-h').each(function () {
        extraHeight_team += $(this).outerHeight(true);
    });

    teamItems.height($(window).height() - Math.ceil(extraHeight_team) -52);

    var listTpl = function (obj) {
        var link = '',
            more = '';
        if (obj.nextRank && obj.id) {
            link = $('body').attr('data-memberList-url-template').replace('ID', obj.id).replace('RANK', obj.nextRank);
            more = '<div class="weui-cell__ft"></div>';
        } else {
            link = 'javascript:;';
        }
        console.log($('body').attr('data-memberList-url-template'), obj.nextRank, obj.id, link);

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

    teamItems.each(function () {
        var self = $(this);
        self.myScroll({
            ajaxUrl: self.attr('data-url'),
            template: listTpl
        });
    })

    $('#lxfwithdrawal').click(function(){
        var now = new Date();
        var t=now.toLocaleString();
        var sbt = t.substr(t.lastIndexOf("/"));
        alert(sbt);
    })
});