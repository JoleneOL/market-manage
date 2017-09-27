$(function () {
    $('.js-editBtn').click(function () {
        $('.delete-btn').toggleClass('open');
        $('.cart-group').toggleClass('cart-group-edit');
    });

    $('#J_allCheck').click(function () {
        $('.js-checkGoods').prop('checked', $(this).is(':checked'));
        countTotal();
    });


    var Quantity = {
        plus: function () {
            var self = this;
            $('.btn-plus').click(function () {
                var $parent = $(this).closest('.edit-quantity');
                var $minus = $parent.find('.btn-minus');
                var $value = $parent.find('.btn-value');
                $minus.removeClass('disabled').prop('disabled', false);
                var val = $value.val();
                val++;
                $value.val(val);
                $parent.siblings('.goods-info')
                    .find('.goods-bought').text('x' + val);
                self.verify(this);
            });
        },
        minus: function () {
            var self = this;
            $('.btn-minus').click(function () {
                var $parent = $(this).closest('.edit-quantity');
                var $plus = $parent.find('.btn-plus');
                var $value = $parent.find('.btn-value');
                $plus.removeClass('disabled').prop('disabled', false);
                var val = $value.val();
                val--;
                $value.val(val);
                $parent.siblings('.goods-info')
                    .find('.goods-bought').text('x' + val);
                self.verify(this);
            });
        },
        focus: function () {
            var self = this;
            $('.btn-value').blur(function () {
                $(this).closest('.edit-quantity')
                    .siblings('.goods-info')
                    .find('.goods-bought').text('x' + $(this).val());
                self.verify(this);
            });
        },
        verify: function (ele) {
            var $parent = $(ele).closest('.edit-quantity');
            var $plus = $parent.find('.btn-plus');
            var $minus = $parent.find('.btn-minus');
            var $value = $parent.find('.btn-value');
            var min = $value.attr('min') || 1;
            var max = $value.attr('max') || 9999;

            if (+$value.val() >= +max) {
                $value.val(max);
                $parent.siblings('.goods-info')
                    .find('.goods-bought').text('x' + $value.val());
                $plus.addClass('disabled').prop('disabled', true);
                $minus.removeClass('disabled').prop('disabled', false);
            } else {
                $plus.removeClass('disabled').prop('disabled', false);
            }
            if (+$value.val() <= +min) {
                $value.val(min);
                $parent.siblings('.goods-info').find('.goods-bought').text('x' + $value.val());
                $minus.addClass('disabled').prop('disabled', true);
                $plus.removeClass('disabled').prop('disabled', false);
            }
            countTotal();
        },
        init: function () {
            this.plus();
            this.minus();
            this.focus();
        }
    };

    Quantity.init();


    $('.js-deleteCart').click(function () {
        var $this = $(this);
        $.confirm("您确定要移除该商品？", function () {
            $this.closest('.cart-group').remove();
            countTotal();
        });
    });

    $('.js-checkGoods').click(function () {
        $('#J_allCheck').prop('checked', false);
        countTotal();
    });

    function countTotal() {
        var total = 0;
        $('.js-checkGoods').each(function () {
            if ($(this).is(':checked')) {
                var $parent = $(this).closest('.cart-group');
                var quantity = Number($parent.find('.btn-value').val());
                var price = Number($parent.data('goods-price'));
                total += quantity * price;
            }
        });

        $('#J_orderTotal').text(total);
    }

    $('#J_settlementBtn').click(function () {
        var order = {};
        $('.js-checkGoods').each(function () {
            if ($(this).is(':checked')) {
                var $parent = $(this).closest('.cart-group');
                var id = $parent.data('goods-id');
                order[id] = $parent.find('.btn-value').val();
            }
        });
        if($('.cart-group').hasClass('cart-group-edit')) {
            $('.js-editBtn').trigger('click');
        }
        updateStorage();
        if ($.isEmptyObject(order)) {
            $.toast("请选择一个商品", "text");
        } else {
            $.showLoading('订单生成中');
            $('input[name="order"]').val(JSON.stringify(order));
            $('#J_orderPlace').submit();
        }
    });

    function updateStorage() {
        var local = {};
        $('.cart-group').each(function () {
            var id = $(this).data('goods-id');
            local[id] = $(this).find('.btn-value').val();
        });
        Storage.setData(local);
    }
});