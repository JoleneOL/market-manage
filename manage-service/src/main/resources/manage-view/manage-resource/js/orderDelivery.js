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
            })
        }
    };

    $('#J_delivery').click(function () {
        // if(!$selectDepot.val()) return layer.msg('请选择仓库');
        getData();
    });

    function getData() {
        var data = {};
        $('#J_form').find('input').not('.js-goods').each(function () {
            data[$(this).attr('name')] = $(this).val();
        });
        var cleckbox = $('.i-checks').find('input');
        data[cleckbox.attr('name')] = cleckbox.is(':checked');
        var goods = [];
        $('.js-goods').each(function () {
            var goodsId = $(this).closest('.form-group').attr('data-goods-id');
            var val = +$(this).val();
            var max = +$(this).attr('max');
            if (val > max || val < 0) {
                layer.msg('发货数量有错误，请检查后再发货');
            }
            goods.push(goodsId + ',' + val);
        });
        data['goods'] = goods;
        console.log(data);
    }
});