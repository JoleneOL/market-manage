$(function () {
    $(".i-checks").iCheck({
        checkboxClass: "icheckbox_square-green",
        radioClass: "iradio_square-green"
    });

    var undeliveredObj = {};
    var depotObj = {};
    var $undelivered = $('#J_undelivered');
    var $depot = $('#J_depot');

    $undelivered.find('tbody').find('tr').each(function () {
        undeliveredObj[$(this).attr('data-goods-id')] = $(this).find('.js-stock').text();
    });


    $depot.find('tbody').find('tr').each(function () {
        var depotId = $(this).attr('data-depot-id');
        var $p = $(this).find('p');
        depotObj[depotId] = {};
        $p.each(function () {
            depotObj[depotId][$(this).attr('data-goods-id')] = $(this).attr('data-stock');
        });
    });

    var $selectDepot = $('#J_selectDepot');
    $selectDepot.change(function () {
        var value = $(this).val();
        Render.goodsList(depotObj[value]);
    });
    var Render = {
        goodsList: function (obj) {
            var parent = $('#J_goodsLists');
            parent.find('.form-group').each(function () {
                var goodId = $(this).attr('data-goods-id');
                var max = +undeliveredObj[goodId] > +obj[goodId] ? obj[goodId] : undeliveredObj[goodId];
                $(this).find('input').attr('max', max);
                $(this).find('.text-danger').text('库存：' + obj[goodId] + '，待发货：' + undeliveredObj[goodId]);
                if (max === 0) $(this).remove();
            })
        },
        depotList: function (obj) {
            $.each(obj, function (i, v) {
                $depot.find('tbody').find('tr').each(function () {
                    var depotId = $(this).attr('data-depot-id');
                    if (depotId == i) {
                        var $p = $(this).find('p');
                        $p.each(function () {
                            var goodsId = $(this).attr('data-goods-id');
                            $(this).attr('data-stock', v[goodsId])
                                .find('.text-danger').text(v[goodsId]);
                        });
                    }

                });
            })
        },
        resetUndelivered: function (data) {
            $.each(data, function (i, v) {
                var arr = v.split(',');
                undeliveredObj[arr[0]] = undeliveredObj[arr[0]] - arr[1];
            });

            $undelivered.find('tbody').find('tr').each(function () {
                $(this).find('.js-stock').text(undeliveredObj[$(this).attr('data-goods-id')]);
            });
            this.complete();
        },
        complete: function () {
            var complete = true;
            $.each(undeliveredObj, function (i, v) {
                if (v > 0) complete = false;
            });
            if (complete) $('#J_delivery').addClass('disabled').prop('disabled', true);
        },
        resetGoodsList: function (value) {
            this.goodsList(depotObj[value]);
            $('#J_goodsLists').find('.js-goods').val(0);
        }
    };

    function handleShipResult(res, formData) {
        if (res.resultCode !== 200) {
            layer.msg(res.resultMsg);
            return false;
        }
        Render.resetUndelivered(formData['goods']);
        depotObj = res.data;
        Render.depotList(res.data);
        Render.resetGoodsList($selectDepot.val());
    }

    $('#J_delivery').click(function () {
        if (!$selectDepot.val()) return layer.msg('请选择仓库');
        var formData = getData();
        if (formData) {
            var loading = layer.load();
            $.ajax('/api/logisticsShip', {
                method: 'POST',
                data: formData,
                dataType: 'json',
                success: function (res) {
                    layer.close(loading);
                    if (res.resultCode === 302) {
                        // 需要 orderNumber
                        layer.prompt({
                            title: res.resultMsg,
                            formType: 0
                        }, function (orderNumber, index) {
                            // 再次提交
                            formData.orderNumber = orderNumber;
                            $.ajax('/api/logisticsShip', {
                                method: 'POST',
                                data: formData,
                                dataType: 'json',
                                success: function (res2) {
                                    layer.close(index);
                                    return handleShipResult(res2, formData);
                                }
                            });
                        });
                        return;
                    }
                    return handleShipResult(res, formData);
                },
                error: function () {
                    layer.close(loading);
                    layer.msg('系统错误，稍后重试')
                }
            })
        }
    });

    function getData() {
        var verify = true;
        var isEmpty = true;
        var data = {};
        $('#J_form').find('input').not('.js-goods').each(function () {
            data[$(this).attr('name')] = $(this).val();
        });
        $('#J_form').find('select').not('.js-goods').each(function () {
            data[$(this).attr('name')] = $(this).val();
        });
        var cleckbox = $('.i-checks').find('input');
        data[cleckbox.attr('name')] = cleckbox.is(':checked');
        var goods = [];
        $('.js-goods').each(function () {
            var goodsId = $(this).closest('.form-group').attr('data-goods-id');
            var val = +$(this).val();
            var max = +$(this).attr('max');
            if (val !== 0) isEmpty = false;
            if (val > max || val < 0) {
                verify = false;
                layer.msg('发货数量有错误，请检查后再发货');
            }
            goods.push(goodsId + ',' + val);
        });
        if (!verify) return '';
        if (isEmpty) {
            layer.msg('不能发送空商品');
            return '';
        }
        data['goods'] = goods;

        return data;
    }

    $('.js-goods').on('input', function () {
        $(this).val($(this).val().replace(/\D/g, ''));
    });

    // goods
    function checkAllGoods() {
        var installation = $('input[name=installation]');
        var goods = $('input[name=goods]').filter(function (_, ele) {
            var ele$ = $(ele);
            var number = +ele$.val();
            return (number > 0 && ele$.data('goods-installation') === 'true');
        });
        //
        if (goods.size() > 0) {
            console.log('存在可安装的货品!');
            installation.prop('readonly', false);
        } else {
            console.log('不存在可安装的货品!');
            installation.attr('readonly', 'readonly');
            // installation.prop('checked', false);
        }
    }

    checkAllGoods();
    var goodsInputs = $('input[name=goods]');
    goodsInputs.change(checkAllGoods);
    goodsInputs.blur(checkAllGoods);

});