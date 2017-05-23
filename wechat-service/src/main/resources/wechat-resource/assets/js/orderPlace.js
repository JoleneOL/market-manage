/**
 * Created by Chang on 2017/5/7.
 */
$(function () {

    var body = $('body');

    $('#recommendId').makeRecommendSelect();

    $('#J_cityPicker').cityPicker({
        title: "请选择收货地址",
        onChange: function () {
            $('#J_cityPicker').closest('.weui-cell').removeClass('weui-cell_warn');
        }
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
        $('input[name="orderTotal"]').val(total);
    }

    function changeAllMoney($ele) {
        var $type = $ele.find('option:checked');
        var deposit = $type.attr('data-deposit');
        var isNeed = $type.attr('data-need-install');
        var model = $type.attr('data-model');
        var cost = $type.attr('data-day-cost');

        var needNumber = 0;
        if (isNeed) needNumber = parseInt(isNeed);

        if (needNumber > 0) {
            $('.js-install').show();
            $('#J_installationCost').find('span').eq(0).text(isNeed);
        } else {
            $('.js-install').hide();
            $('#J_installationCost').find('span').eq(0).text(0);
        }
        $('#J_userDeposit').find('span').eq(0).text(deposit);
        $('#J_leasedType').val(model);
        $('#J_package').val('服务费 ' + cost + '元 / 天');
    }


    // 粗略的手机号正则
    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1(3|4|5|7|8)\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");


    $.validator.setDefaults({
        submitHandler: function (form) {
            form.submit();
        }
    });

    $('#J_form').validate({
        rules: {
            name: "required",
            age: {
                required: true,
                number: true,
                digits: true
            },
            address: 'required',
            fullAddress: 'required',
            mobile: {
                required: true,
                isPhone: true
            },
            amount: {
                required: true
            },
            isAgree: "required"
        },
        messages: {
            name: "请填写客户姓名",
            age: {
                required: "请填写年龄",
                digits: "请输入整数"
            },
            mobile: {
                required: "请填写手机号码"
            },
            address: "请选择地址",
            fullAddress: "请填写详细地址",
            amount: {
                required: "请填写购买数量"
            }
        },
        errorPlacement: function (error, element) {
            // $.toptip(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').addClass("weui-cell_warn")
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').removeClass("weui-cell_warn");
        }
    });
});