/**
 * Created by Chang on 2017/5/7.
 */
$(function () {

    var body = $('body');

    $('#recommendId').makeRecommendSelect();

    $('#J_cityPicker, #J_invoiceAddress').cityPicker({
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
        // var deposit = +$('#J_userDeposit').find('span').eq(0).text();
        var deposit = +$('#J_userCost').find('span').eq(0).text();
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
        $('#J_userCost').find('span').eq(0).text(deposit);
        $('#J_leasedType').val(model);

        // $('#J_userDeposit').find('span').eq(0).text(deposit);
        // $('#J_package').val('服务费 ' + cost + '元 / 天');
    }

    var $mortgageCode = $('#J_mortgageCode');
    var isValid = $('input[name="isValid"]');
    $mortgageCode.on('keyup mouseout input', function () {
        var $this = $(this);
        var v = $this.val();
        if (v) {
            isValid.rules('add', {
                required: true,
                messages: {
                    required: "校验按揭码失败"
                }
            });
        } else {
            isValid.rules('remove');
            isValid.val('');
        }
    });

    $('#J_checkBtn').click(function () {
        var mortgageCode = $mortgageCode.val();
        if (!mortgageCode) return '';
        $.showLoading('数据校验中');
        $.ajax('/api/mortgageCode', {
            method: 'POST',
            data: {
                mortgageCode: mortgageCode
            },
            dataType: 'json',
            success: function (data) {
                if (data.resultCode === 400) {
                    $.toptip(data.resultMsg);
                    isValid.val('');
                    $.hideLoading();
                    return false;
                }
                if (data.resultCode !== 200) {
                    $.toptip("发送失败，请重试");
                    $.hideLoading();
                    return false;
                }
                $.hideLoading();
                $.toptip("校验成功", "success");
                isValid.val('ok');
            },
            error: function () {
                $.hideLoading();
                $.toptip("系统错误");
            }
        })
    });
    // 粗略的手机号正则
    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1([34578])\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");


    $('#J_form').validate({
        ignore: "",
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
            recommend: 'required',
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
            recommend: '请填写该项',
            address: "请选择地址",
            fullAddress: "请填写详细地址",
            amount: {
                required: "请填写购买数量"
            },
            isAgree: "请同意《用户协议》"
        },
        errorPlacement: function (error, element) {
            $.toptip(error, 1000);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').addClass("weui-cell_warn")
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').removeClass("weui-cell_warn");
        },
        submitHandler: function (form) {
            form.submit();
        }
    });
    $('#J_needInvoice').click(function () {
        var that = $(this);
        $.actions({
            title: "选择是否开票",
            actions: [
                {
                    text: "填写发票信息",
                    className: "text-custom",
                    onClick: function () {
                        invoiceFunc.setData();
                        $('#J_editInvoice').popup();
                    }
                },
                {
                    text: "不需要发票",
                    onClick: function () {
                        that.html('不需要发票');
                        disableInput();
                    }
                }
            ]
        })
    });
    $('#J_invoiceOK').click(function () {
        var flag = $("#J_invoiceForm").valid();
        if(flag) {
            invoiceFunc.setCompany();
            invoiceFunc.setHiddenData();
            $.closePopup();
        }
    });
    $('#J_invoiceCancel').click(function () {
        $.closePopup();
        invoiceFunc.clearWarn();
    });
    function disableInput() {
        $('#J_invoiceArea').find('input[type="hidden"]').each(function () {
            $(this).prop('disabled', true)
        });
    }

    $('#J_invoiceForm').validate({
        rules: {
            company: "required",
            invoiceAddressee: 'required',
            invoiceAddress: 'required',
            invoiceFullAddress: "required",
            invoiceMobile: {
                required: true,
                isPhone: true
            }
        },
        messages: {
            company: "请填写公司抬头",
            invoiceAddressee: "请填写公司抬头",
            invoiceAddress: "请填写收件地址",
            invoiceFullAddress: "请填写详细地址",
            invoiceMobile: {
                required: "请填写联系电话"
            }
        },
        errorPlacement: function (error, element) {
            $.toptip(error, 1000);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').addClass("weui-cell_warn")
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').removeClass("weui-cell_warn");
        }
    });
    var invoiceFunc = {
        addressee: $('.js-invoiceAddressee'),
        address: $('.js-invoiceAddress'),
        fullAddress: $('.js-invoiceFullAddress'),
        mobile: $('.js-invoiceMobile'),
        setData: function () {
            if (!this.addressee.val()) this.addressee.val($('input[name="name"]').val());
            if (!this.address.val()) this.address.val($('input[name="address"]').val());
            if (!this.fullAddress.val()) this.fullAddress.val($('input[name="fullAddress"]').val());
            if (!this.mobile.val()) this.mobile.val($('input[name="mobile"]').val());
            $('.js-invoiceTotal').val('￥' + $('input[name="orderTotal"]').val())
        },
        clearWarn: function () {
            $('#J_editInvoice').find('.weui-cell').removeClass("weui-cell_warn");
        },
        setHiddenData: function () {
            $('#J_invoiceArea').find('input[type="hidden"]').each(function () {
                var name = $(this).attr('name');
                $(this).val($('#J_invoiceForm').find('input[name="'+name+'"]').val()).prop('disabled', false);
            });
        },
        setCompany: function () {
            $('#J_needInvoice').html($('#J_invoiceForm').find('.js-company').val());
        }
    };
});