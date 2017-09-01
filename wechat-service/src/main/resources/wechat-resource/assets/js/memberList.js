$(function () {

    var infiniteWrap = $('.view-scroll-wrap');

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
            '            <div class="ellipsis">' + obj.phone + '</div>\n' +
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