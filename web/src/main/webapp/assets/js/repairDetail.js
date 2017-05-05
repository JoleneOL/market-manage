/**
 * Created by Neo on 2017/5/5.
 */
$(function () {
    "use strict";
    
    $('.img-feedback-list').find('img').click(function () {
        var $img = $('<img/>').attr('src', $(this).attr('src'));
        var content = $('<div>').append($img);
        window.top.layer.open({
            type: 1,
            shade: 0.5,
            title: false,
            area: ['auto', 'auto'],
            content: content.html()
        });
    });
});