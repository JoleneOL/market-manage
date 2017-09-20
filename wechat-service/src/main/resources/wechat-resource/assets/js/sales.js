$(function () {

    $("#J_date").calendar({
        maxDate: new Date(),
        onChange: function (p, values, displayValues) {
            console.log(values, displayValues);
        }
    });

    $('#J_filter').click(function () {
        console.log($('input[name="date"]').val());
        console.log($('input[name="remark"]').is(':checked'));
        console.log($('input[name="deal"]').is(':checked'));
    });

    var infiniteWrap = $('.view-scroll-wrap');

    var salesTpl = function (obj) {
        var statusCode = (obj.statusCode === 1) ? '<span>已成交</span>' : '<span class="text-error">未成交</span>';
        return '<a class="weui-cell weui-cell_access view-sales-item popup-btn js-openPopup"' +
            '        data-id="' + obj.id + '"' +
            '        data-json="' + JSON.stringify(obj).replace(/"/g, "'") + '">' +
            '        <div class="weui-cell__bd">' +
            '            <p>' + obj.name + '</p>' +
            '            <p class="view-sales-time">' + obj.time + '</p>' +
            '        </div>' +
            '        <div class="view-sales-remark">' + obj.remark + '</div>' +
            '        <div class="weui-cell__ft">' + statusCode + '</div>' +
            '    </a>';
    };

    var extraHeight_team = 0;
    $('.js-extra-h').each(function () {
        extraHeight_team += $(this).outerHeight(true);
    });

    infiniteWrap.height($(window).height() - Math.ceil(extraHeight_team));

    infiniteWrap.myScroll({
        debug: true,
        ajaxUrl: infiniteWrap.attr('data-url'),
        template: salesTpl
    });

    $(document).on('tap click', '.js-openPopup', function () {
        var parent = $(this).closest('.view-sales-item');
        var json = parent.attr('data-json').replace(/'/g, '"');
        var _html = template('J_popupTpl', JSON.parse(json));

        $('#J_popupContainer').html(_html);
        $('#J_popup').popup();
    }).on('click', '#J_editRemark', function () {
        var self = $(this);
        var id = self.data('id');
        $.prompt({
            title: "输入绩效备注",
            onOK: function (text) {
                updateRemark(id, text, self);
            },
            onCancel: function () {
                console.log("取消了");
            }
        });
    });

    function updateRemark(id, text, ele) {
        $.showLoading();
        $.ajax('/remark/' + id, {
            method: 'PUT',
            data: {
                remark: text
            },
            dataType: 'json',
            success: function (res) {
                console.log(res);
                $.hideLoading();
                if (res.resultCode !== 200) {
                    return $.toptip(res.resultMsg);
                }
                renderDOM(ele, text);
                $.alert("绩效备注是:" + text, "设定成功");
            },
            error: function () {
                $.hideLoading();
                $.toptip('系统出现故障，稍后再试');
            }
        })
    }

    function renderDOM(ele, text) {
        var id = ele.data('id');
        ele.parent('.weui-form-preview__value').find('.js-remark').text(text);
        var $ele = $('.view-sales-item[data-id="' + id + '"]');
        $ele.find('.view-sales-remark').text(text);
        var json = JSON.parse($ele.data('json').replace(/'/g, '"'));
        json.remark = text;
        var new_json = JSON.stringify(json).replace(/"/g, "'");
        $ele.attr('data-json', new_json);
    }
});