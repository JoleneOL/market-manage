$(function () {

    $("#J_date").calendar({
        maxDate: new Date(),
        onChange: function (p, values, displayValues) {
            console.log(values, displayValues);
        }
    });

    var infiniteWrap = $('.view-scroll-wrap');

    var extraHeight_team = 0;
    $('.js-extra-h').each(function () {
        extraHeight_team += $(this).outerHeight(true);
    });

    infiniteWrap.height($(window).height() - Math.ceil(extraHeight_team));

    infiniteWrap.myScroll({
        ajaxUrl: infiniteWrap.attr('data-url'),
        template: function () {
            return '<p>1</p>'
        }
    });

});