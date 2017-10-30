/**
 * 推荐人
 * 1, 依赖select2
 * Created by CJ on 16/05/2017.
 */
$(function () {
    $.makeRecommendSelect = function (element, options) {
        options = options || {};

        // console.log('makeRecommendSelect', options, $('body').attr('data-search-login-url'));

        element.select2({
            theme: options.theme || "bootstrap",
            width: null,
            containerCssClass: ':all:',
            placeholder: options.placeholder || "选择一个用户",
            allowClear: true,
            language: "zh-CN",
            ajax: {
                url: options.url || $('body').attr('data-search-login-url'),
                dataType: 'json',
                delay: 250,
                data: function (params) {
                    return {
                        search: params.term, // search term
                        page: params.page
                    };
                },
                processResults: function (data, params) {
                    // parse the results into the format expected by Select2
                    // since we are using custom formatting functions we do not need to
                    // alter the remote JSON data, except to indicate that infinite
                    // scrolling can be used
                    params.page = params.page || 1;

                    return {
                        results: data.items,
                        pagination: {
                            more: (params.page * 30) < data.total_count
                        }
                    };
                },
                cache: true
            },
            escapeMarkup: function (markup) {
                return markup;
            }, // let our custom formatter work
            minimumInputLength: 2,
            templateResult: function (x) {
                if (x.id == '')
                    return x.text;
                // 渲染html宽体
                // console.log('templateResult', x);
                var strName;
                if (x.name)
                    strName = '<p>姓名:' + x.name + '</p>';
                else
                    strName = '';

                var strMobile;
                if (x.mobile)
                    strMobile = '<p>电话:' + x.mobile + '</p>';
                else
                    strMobile = '';

                return strName + strMobile;
            },
            templateSelection: function (x) {
                if (x.id == '')
                    return x.text;
                // 渲染当前选择
                if (x.name && x.mobile)
                    return x.name + '(' + x.mobile + ')';
                if (!x.name && !x.mobile)
                    return '';
                if (x.name)
                    return x.name;
                return x.mobile;
            }

        });
    };
    var $sS = $.makeRecommendSelect;
    $sS.fn = $sS.prototype = {
        version: '0.0.1'
    };
    $sS.fn.extend = $sS.extend = $.extend;
    $sS.fn.extend({});
    $.fn.makeRecommendSelect = function (options) {
        this.each(function () {
            var sS = new $sS($(this), options);
        });
        return this;
    };
});
