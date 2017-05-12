/**
 * Created by Neo on 2017/5/12.
 */
;(function () {
    "use strict";

    $.fn.myScroll = function (options) {
        var s = $.extend({
            debug: false,
            threshold: 65,
            loading: false,
            ajaxUrl: '',
            ajaxMethod: 'GET',
            ajaxData: {},
            loadSuccess: function () {},
            template: function () {
                console.error("需要模板代码");
            }
        }, options);

        // jquery对象
        var $self = this;
        // 原生DOM对象
        var self = this[0];

        var myScroll = new IScroll(self, {
            mouseWheel: true,
            probeType: 3,
            click: true
        });

        function isPassive() {
            var supportsPassiveOption = false;
            try {
                addEventListener("neochang", null, Object.defineProperty({}, 'passive', {
                    get: function () {
                        supportsPassiveOption = true;
                    }
                }));
            } catch (e) {
            }
            return supportsPassiveOption;
        }

        document.addEventListener('touchmove', function (e) {
            e.preventDefault();
        }, isPassive() ? {
            capture: false,
            passive: false
        } : false);


        myScroll.on("scroll", function () {
            if (-(this.maxScrollY - this.y) < s.threshold) {
                infiniteGet();
            }
        });

        function infiniteGet() {
            if (s.loading) return;
            s.loading = true;
            getDate();
        }

        function getDate() {
            if(!s.ajaxUrl) {
                console.error('需要数据请求地址');
                return '';
            }
            $.ajax(s.ajaxUrl, {
                method: s.ajaxMethod,
                data: s.ajaxData,
                dataType: 'json',
                success: function (res) {
                    if (res.resultCode !== 200) {
                        $.toast('请求失败', 'cancel');
                        s.loading = false;
                        return '';
                    }
                    if (res.data.length > 0) {
                        $self.find('.weui-loadmore').before(createDom(res.data));
                        myScroll.refresh();
                        s.loading = false;
                    } else {
                        $self.find('.weui-loadmore').text('没有更多内容了');
                        s.loading = true;
                    }
                },
                error: function () {
                    $.toast('服务器异常', 'cancel');
                }
            });
        }

        function createDom(obj) {
            var domStr = '';
            obj.forEach(function (v) {
                domStr += s.template(v);
            });
            return domStr;
        }
    };
})(jQuery);