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

    //查找推荐人

    $('#J_recommend').on('input', function () {
        var v = $(this).val();
        if (/^1([34578])\d{9}$/.test(v)) {
            searchRecommend(v);
        }
    });

    function searchRecommend(mobile) {
        $.ajax('/api/recommend', {
            method: 'POST',
            data: {mobile: mobile},
            dataType: 'json',
            success: function (res) {
                if (res.resultCode !== 200) {
                    $.toast('查询失败', 'cancel');
                    return '';
                }
                setInviter(res.data.userName);
            },
            error: function () {
                $.toast('服务器异常', 'cancel');
            }
        });
    }

    function setInviter(val) {
        var $inviter = $('#J_inviter');
        var $parent = $inviter.closest('.weui-cell');
        console.log(val);
        $inviter.val(val);
        if (val) {
            $parent.removeClass("weui-cell_warn");
        } else {
            $parent.addClass("weui-cell_warn");
        }
    }

    // 粗略的手机号正则
    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1([34578])\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");

    // 表单提交确认页面
    function orderDetailTpl() {
        return '<div class="view-order"> ' +
            '<div class="view-order-items"> ' +
            '<span>订单总金额</span> ' +
            '<p><span>'+ $('input[name="orderTotal"]').val()+'</span></p> ' +
            '</div> ' +
            '<div class="view-order-items"> ' +
            '<span>姓名</span> ' +
            '<p><span>'+ $('input[name="name"]').val()+'</span></p> ' +
            '</div> ' +
            '<div class="view-order-items"> ' +
            '<span>电话</span> ' +
            '<p><span class="mobile">'+ $('input[name="mobile"]').val()+'</span></p> ' +
            '</div> ' +
            '<div class="view-order-items"> ' +
            '<span>安装地址</span> ' +
            '<p><span class="address">'+ $('input[name="address"]').val()+'</span><span class="fullAddress">'+ $('input[name="fullAddress"]').val()+'</span></p> ' +
            '</div> ' +
            '<div class="view-order-items"> ' +
            '<span>产品</span> ' +
            '<p><span class="goodId">'+ $('select[name="goodId"]').find('option:selected').text()+'</span><span class="leasedType">'+ $('input[name="leasedType"]').val()+'</span></p> ' +
            '</div> ' +
            '<div class="view-order-items"> ' +
            '<span>数量</span> ' +
            '<p><span class="amount">'+ $('input[name="amount"]').val()+'</span></p> ' +
            '</div> ' +
            '<div class="view-order-items"> ' +
            '<span>推荐人</span> ' +
            '<p><span class="inviter">'+ $('input[name="inviter"]').val()+'</span><span class="recommend">'+ $('input[name="recommend"]').val()+'</span></p> ' +
            '</div> ' +
            '</div>';
    }

    $.validator.setDefaults({
        submitHandler: function (form) {

            $.modal({
                title: "订单确认",
                text: orderDetailTpl(),
                buttons: [
                    {
                        text: "确认",
                        onClick: function () {
                            form.submit();
                        }
                    },
                    {text: "修改", className: "default"}
                ]
            });
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
            recommend: {
                required: true,
                isPhone: true
            },
            inviter: 'required',
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
            address: "请选择地址",
            fullAddress: "请填写详细地址",
            mobile: {
                required: "请填写手机号码"
            },
            recommend: {
                required: "请填写推荐人手机号"
            },
            amount: "请填写购买数量"
        },
        errorPlacement: function (error, element) {
        },
        highlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').addClass("weui-cell_warn")
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').removeClass("weui-cell_warn");
        }
    });
});