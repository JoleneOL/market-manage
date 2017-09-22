$(function () {
    $('#J_searchInput').on('keypress', function (e) {
        var self = $(this);
        var keyCode = e.keyCode;
        var input = $(this).val();
        // if (!input) return '';
        if (keyCode === 13) {
            e.preventDefault();
            $.showLoading();
            $.ajax($('body').attr('data-goodsSearch-url'), {
                method: 'GET',
                data: {
                    search: input
                },
                dataType: 'json',
                success: function (res) {
                    $.hideLoading();
                    if (res.resultCode !== 200) {
                        return $.toptip(res.resultMsg);
                    }
                    var _html = template('J_resultTpl', res);
                    $('#J_resultContainer').html(_html);
                    self.blur();
                },
                error: function () {
                    $.hideLoading();
                    $.toptip('系统出现故障，稍后再试');
                }
            })
        }
    });

    $('.js-clearBtn').click(function (e) {
        $(e.target).parent('.search-bar_box').find('.search-bar_input').val('').focus();
    });
});