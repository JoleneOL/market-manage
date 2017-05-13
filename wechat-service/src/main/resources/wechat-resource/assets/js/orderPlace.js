/**
 * Created by Chang on 2017/5/7.
 */
$(function () {
    $('#J_cityPicker').cityPicker({
        title: "请选择收货地址"
    });

    $('#J_goodsAmount').keyup(function () {
        countTotal();
    });

    $('#J_goodsType').change(function () {
        changeAllMoney($(this));
        countTotal();
    });


    function countTotal() {
        var deposit = +$('#J_userDeposit').find('span').eq(0).text();
        var cost = +$('#J_installationCost').find('span').eq(0).text();
        var amout = +$('#J_goodsAmount').val();
        var total = (deposit + cost) * amout;
        $('#J_orderTotal').find('strong').text(total);
    }

    function changeAllMoney($ele) {
        var $type = $ele.find('option:checked');
        var deposit = $type.attr('data-deposit');
        var isNeed = $type.attr('data-need-install');
        var model = $type.attr('data-model');

        if (isNeed) {
            $('.js-install').show();
            $('#J_installationCost').find('span').eq(0).text(isNeed);
        } else {
            $('.js-install').hide();
            $('#J_installationCost').find('span').eq(0).text(0);
        }
        $('#J_userDeposit').find('span').eq(0).text(deposit);
        $('#J_leasedType').val(model);
    }
});