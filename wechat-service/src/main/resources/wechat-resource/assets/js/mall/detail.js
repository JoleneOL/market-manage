$(function () {
    var swiper = new Swiper('.swiper-container', {
        pagination: '.swiper-pagination',
        autoplay: 3000,
        slidesPerView: 1,
        paginationClickable: true,
        observer: true,
        observeParents: true,
        updateOnImagesReady: true,
        loop: true
    });
    var animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend';

    $('.js-open-menu').click(function () {
        $('.flick-menu-mask').show();
        $('.spec-menu').show().addClass('spec-menu-show').one(animationEnd, function () {
            $(this).removeClass('spec-menu-show');
        });
    });

    $('.js-closed-menu').click(function () {
        $('.spec-menu').addClass('spec-menu-hide').one(animationEnd, function () {
            $(this).hide().removeClass('spec-menu-hide');
            $('.flick-menu-mask').hide();
        });
    });


    $('.property-list').find('li').click(function () {
        if ($(this).hasClass('disabled')) return '';
        $(this).toggleClass('active').siblings().removeClass('active');
        if ($(this).hasClass('active')) {
            $('.js-goodsStock').text($(this).data('goods-stock'));
            Quantity.setMax($(this).data('goods-stock'));
            Quantity.verify();
        } else {
            $('.js-goodsStock').text('');
        }
    });


    var Quantity = {
        input: $('.btn-input').find('input'),
        setMax: function (max) {
            $('.btn-plus').find('a').attr('data-max', max);
        },
        plus: function () {
            var self = this;
            $('.btn-plus').find('a').click(function () {
                if ($(this).parent().hasClass('off')) return '';
                $('.btn-minus').removeClass('off');
                var val = self.input.val();
                val++;
                self.input.val(val);

                self.verify();
            });
        },
        minus: function () {
            var self = this;
            $('.btn-minus').find('a').click(function () {
                if ($(this).parent().hasClass('off')) return '';
                $('.btn-plus').removeClass('off');
                var val = self.input.val();
                val--;
                self.input.val(val);

                self.verify();
            });
        },
        focus: function () {
            var self = this;
            this.input.blur(function () {
                self.verify();
            });
        },
        verify: function () {
            var plus = $('.btn-plus');
            var minus = $('.btn-minus');
            var min = minus.find('a').attr('data-min');
            var max = plus.find('a').attr('data-max');

            if (+this.input.val() >= +max) {
                this.input.val(max);
                plus.addClass('off');
                minus.removeClass('off');
            } else {
                plus.removeClass('off');
            }
            if (+this.input.val() <= +min) {
                this.input.val(min);
                minus.addClass('off');
                plus.removeClass('off');
            }
        },
        init: function () {
            this.plus();
            this.minus();
            this.focus();
        }
    };

    Quantity.init();

    $('#J_buyNow').click(function () {
        var $li = $('.property-list').find('.active');
        if($li.length === 0) return $.toast("请选择一种颜色", "text");
        $.showLoading('订单生成中');
        var order = {};
        order[$li.data('goods-id')] = $('.btn-input').find('input').val();
        $('input[name="order"]').val(JSON.stringify(order));
        $('#J_orderPlace').submit();
    });

    var local = {};
    $('#J_addCart').click(function () {
        var $li = $('.property-list').find('.active');
        if($li.length === 0) return $.toast("请选择一种颜色", "text");
        local[$li.data('goods-id')] = $('.btn-input').find('input').val();
        Storage.setData(local);
        $.toast("添加成功", "text");
    });
});