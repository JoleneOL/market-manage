/**
 * Created by Chang on 2017/5/7.
 */
$(function () {

    var infiniteWrap = $('#J_teamList');

    var extraHeight = 0;
    $('.js-extra-h').each(function () {
        extraHeight += $(this).outerHeight(true);
    });

    infiniteWrap.height($(window).height() - Math.ceil(extraHeight));

    infiniteWrap.myScroll({
        ajaxUrl: '/api/teamList',
        template: function (obj) {
            return '<div class="view-list-item">' +
                '<div>' + obj.name + '</div>' +
                '<div>' + obj.rank + '</div>' +
                '<div>' + obj.joinTime + '</div>' +
                '<div>' + obj.phone + '</div>' +
                '</div>';
        }
    });
});

