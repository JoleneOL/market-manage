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

    var listTpl = function (obj) {
        return '<div class="view-list-item">' +
            '<div>' + obj.name + '</div>' +
            '<div>' + obj.rank + '</div>' +
            '<div>' + obj.joinTime + '</div>' +
            '<div>' + obj.phone + '</div>' +
            '</div>';
    };

    var myScroll = infiniteWrap.myScroll({
        debug: true,
        ajaxUrl: infiniteWrap.attr('data-url'),
        ajaxData: {
            rank: infiniteWrap.attr('data-rank')
        },
        page: +infiniteWrap.attr('data-page'),
        template: listTpl
    });

    $('#J_changeRank').click(function () {
        $.actions({
            title: "选择级别",
            actions: [
                {
                    text: "全部",
                    className: "color-primary",
                    onClick: function () {
                        $.showLoading();
                        getRankList('all');
                    }
                },
                {
                    text: "总代理",
                    onClick: function () {
                        $.showLoading();
                        getRankList(1);
                    }
                },
                {
                    text: "分代理",
                    onClick: function () {
                        $.showLoading();
                        getRankList(2);
                    }
                },
                {
                    text: "经销商",
                    onClick: function () {
                        $.showLoading();
                        getRankList(3);
                    }
                },
                {
                    text: "爱心天使",
                    onClick: function () {
                        $.showLoading();
                        getRankList(4);
                    }
                }
            ]
        });
    });


    function getRankList(data) {
        var url = infiniteWrap.attr('data-url');
        $.ajax(url, {
            method: "GET",
            data: {rank: data, page: 1},
            dataType: 'json',
            success: function (res) {
                if (res.resultCode !== 200) {
                    $.toast('请求失败', 'cancel');
                    return '';
                }
                setRankList(res.data);
                $.hideLoading();
                infiniteWrap.attr('data-rank', data);
                myScroll.reset({
                    ajaxData: {
                        rank: infiniteWrap.attr('data-rank')
                    },
                    page: 1
                });
                $.myScrollRefresh(true);
            },
            error: function () {
                $.toast('服务器异常', 'cancel');
            }
        });
    }

    function setRankList(obj) {
        var domStr = '';
        if (obj.length > 0) {
            obj.forEach(function (v) {
                domStr += listTpl(v);
            });
        } else {
            domStr = '<div class="view-list-item view-no-res"><p class="text-center">暂无数据</p></div>'
        }
        infiniteWrap
            .find('.view-list-item').remove()
            .end()
            .find('.weui-loadmore').before(domStr);
    }
});

