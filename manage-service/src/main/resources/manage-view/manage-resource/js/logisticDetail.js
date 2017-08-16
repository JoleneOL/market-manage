$(function () {
    $(document).on('mouseenter', '.text', function () {
        var detail = $(this).find('.hide');
        if (detail.length > 0) {
            layer.msg(detail.html(), {
                offset: '20%'
            });
        }

    }).on('mouseleave', '.text', function () {
        layer.closeAll();
    });

    var time = '';
    $('.date').each(function () {
        $(this).text() !== time ? time = $(this).text() : $(this).addClass('invisible').siblings('.week').addClass('invisible');
    })
});