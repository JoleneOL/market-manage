/**
 * Created by Neo on 2017/6/30.
 */
$(function () {
    "use strict";

    $('.img-feedback-list').find('img').click(function () {
        var $img = $('<img class="img-feedback-big img-thumbnail"/>').attr('src', $(this).attr('src'));
        var content = $('<div class="container">').append($img);
        window.top.layer.open({
            type: 1,
            shade: 0.5,
            title: false,
            shadeClose: true,
            area: ['auto', 'auto'],
            content: content.html()
        });
    });

    $('#J_datePicker').flatpickr({
        minDate: new Date(),
        locale: 'zh'
    });

    $('#J_timePicker').flatpickr({
        minDate: new Date(),
        enableTime: true,
        noCalendar: true,
        enableSeconds: false,
        time_24hr: true,
        dateFormat: "H:i",
        minuteIncrement: 1,
        locale: 'zh'
    });
});