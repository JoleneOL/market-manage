$(function () {

    $("#J_date").calendar({
        maxDate: new Date(),
        onChange: function (p, values, displayValues) {
            console.log(values, displayValues);
            $('#J_clear').show();
        }
    });

    $('input[type=checkbox][data-group]').change(function () {
        // 如果当前被选中，那么另一个必须不被选中
        var self = $(this);
        if (self.is(':checked')) {
            var group = self.data('group');
            var theOther = $('input[type=checkbox][data-group=' + group + ']').not('[name=' + self.attr('name') + ']');
            theOther.prop('checked', false);
        }
    });

    $('#J_filter').click(function () {
        var _date = $('input[name="date"]').val();
        var _remark = toCheckBoxValue('remark');
        var _deal = toCheckBoxValue('deal');

        var url = infiniteWrap.attr('data-url');
        $.showLoading();
        $.ajax(url, {
            method: "GET",
            data: {
                date: _date,
                remark: _remark,
                deal: _deal,
                page: 0
            },
            dataType: 'json',
            success: function (res) {
                if (res.resultCode !== 200) {
                    $.toast('请求失败', 'cancel');
                    return '';
                }
                setSalesList(res.data);
                $.hideLoading();
                myScroll.reset({
                    ajaxData: {
                        date: _date,
                        remark: _remark,
                        deal: _deal
                    },
                    page: 1
                });
                $.myScrollRefresh(true);
            },
            error: function () {
                $.toast('服务器异常', 'cancel');
            }
        });

    });

    function setSalesList(obj) {
        var domStr = '';
        if (obj.length > 0) {
            obj.forEach(function (v) {
                domStr += salesTpl(v);
            });
            infiniteWrap
                .find('.view-sales-item').remove()
                .end()
                .find('.weui-loadmore').html('<i class="weui-loading"></i><span class="weui-loadmore__tips">数据加载中...</span>').before(domStr);
        } else {
            infiniteWrap
                .find('.view-sales-item').remove()
                .end()
                .find('.weui-loadmore').html('没有更多内容了');
        }

    }

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

    /**
     * 如果未曾抉择则返回空字符串；如果选择肯定则返回true反之false
     * @param group data-group的名字
     */
    function toCheckBoxValue(group) {
        // is(':checked')
        if ($('input[type=checkbox][data-group=' + group + '][name*=True]').is(':checked'))
            return 'true';
        if ($('input[type=checkbox][data-group=' + group + '][name*=False]').is(':checked'))
            return 'false';
        return '';
    }

    var myScroll = infiniteWrap.myScroll({
        // debug: true,
        ajaxUrl: infiniteWrap.attr('data-url'),
        ajaxData: {
            date: $('input[name="date"]').val(),
            //
            remark: toCheckBoxValue('remark'),
            deal: toCheckBoxValue('deal')
        },
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
        $.ajax('/salesAchievement/' + id + '/remark', {
            method: 'PUT',
            data: text,
            contentType: 'text/plain; charset=UTF-8',
            dataType: 'json',
            success: function () {
                $.hideLoading();
                // if (res.resultCode !== 200) {
                //     return $.toptip(res.resultMsg);
                // }
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

    $('#J_clear').click(function () {
        $(this).closest('.weui-cell').find('input[name="date"]').val('');
        $(this).hide();
    })
});