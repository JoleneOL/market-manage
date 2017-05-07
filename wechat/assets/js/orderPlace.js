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
        var deposit = +$('#J_userDeposit').find('strong').text();
        var cost = +$('#J_installationCost').find('strong').text();
        var amout = +$('#J_goodsAmount').val();
        var total = (deposit + cost) * amout;
        $('#J_orderTotal').find('strong').text(total);
    }

    function changeAllMoney($ele) {
        var $type = $ele.find('option:checked');
        var deposit = $type.attr('data-deposit');
        var isNeed = $type.attr('data-need-install');
        if (isNeed) {
            $('.js-install').show();
            $('#J_installationCost').find('strong').text(isNeed);
        } else {
            $('.js-install').hide();
            $('#J_installationCost').find('strong').text(0);
        }
        $('#J_userDeposit').find('strong').text(deposit);
    }
});