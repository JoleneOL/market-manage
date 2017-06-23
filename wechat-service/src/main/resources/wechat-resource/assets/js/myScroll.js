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
            isAjax: true,
            ajaxData: {},
            page: 0,
            loadSuccess: function () {
            },
            tabRefresh: function () {
            },
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

        function getDate(obj) {
            var extra = obj || {};
            if (!s.ajaxUrl) {
                console.error('需要数据请求地址');
                return '';
            }
            s.ajaxData.page = s.page;
            if (s.debug) console.info(s.ajaxData);
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
                    if (res.data && res.data.length > 0) {
                        $self.find('.weui-loadmore').before(createDom(res.data));
                        myScroll.refresh();
                        s.page++;
                        s.loading = false;
                    } else {
                        $self.find('.weui-loadmore').text('没有更多内容了');
                        s.loading = true;
                    }
                    if (extra.again) getDate();
                },
                error: function () {
                    $.toast('服务器异常', 'cancel');
                }
            });
        }

        // 判断是客户端渲染还是服务器渲染
        if (s.isAjax) {
            var extra = {};
            extra.again = true;
            if (s.loading) return;
            s.loading = true;
            getDate(extra);
        } else {
            // 服务器渲染的话，加载从第二页开始
            s.page = 1;
        }

        function createDom(obj) {
            var domStr = '';
            obj.forEach(function (v) {
                domStr += s.template(v);
            });
            return domStr;
        }

        $.extend({
            myScrollRefresh: function (goTop) {
                if (goTop === true) myScroll.scrollTo(0, 0);
                myScroll.refresh();
            }
        });

        $self.reset = function (op) {
            s = $.extend(s, op);
        };

        $self.reload = function (op) {
            if (s.isAjax) {
                op.page = 0;
            } else {
                op.page = 1;
            }
            s = $.extend(s, op);
            $self.find(op.removeEle).remove();
            getDate(extra);
        };

        s.tabRefresh(myScroll);

        return $self;

    };


})(jQuery);
