$(function () {

    $("#J_date").calendar({
        maxDate: new Date(),
        onChange: function (p, values, displayValues) {
            console.log(values, displayValues);
            $('#J_clear').show();
        }
    });

    $('#J_filter').click(function () {
        var _date = $('input[name="date"]').val();
        var _remark = $('input[name="remark"]').is(':checked');
        var _deal = $('input[name="deal"]').is(':checked');

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
                setRankList(res.data);
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

    function setRankList(obj) {
        var domStr = '';
        if (obj.length > 0) {
            obj.forEach(function (v) {
                domStr += salesTpl(v);
            });
        } else {
            domStr = '<div class="view-list-item view-no-res"><p class="text-center">暂无数据</p></div>'
        }
        infiniteWrap
            .find('.view-sales-item').remove()
            .end()
            .find('.weui-loadmore').before(domStr);
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

    var myScroll = infiniteWrap.myScroll({
        // debug: true,
        ajaxUrl: infiniteWrap.attr('data-url'),
        ajaxData: {
            date: $('input[name="date"]').val(),
            remark: $('input[name="remark"]').is(':checked'),
            deal: $('input[name="deal"]').is(':checked')
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
        $.ajax('/remark/' + id, {
            method: 'PUT',
            data: {
                remark: text
            },
            dataType: 'json',
            success: function (res) {
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

    $('#J_clear').click(function () {
        $(this).closest('.weui-cell').find('input[name="date"]').val('');
        $(this).hide();
    })
});