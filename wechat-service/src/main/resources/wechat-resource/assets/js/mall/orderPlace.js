$(function () {
    $('#J_cityPicker, #J_invoiceAddress').cityPicker({
        title: "请选择收货地址",
        onChange: function () {
            $('#J_cityPicker').closest('.weui-cell').removeClass('weui-cell_warn');
        }
    });

    // 花呗分期相关
    var installment = $('#J_installment');
    var submitBtn = $('#J_submitBtn');
    var info = $('#J_installmentInfo');
    installment.change(function () {
        installmentFunc($('#J_orderTotal').find('strong').text());
        if ($(this).is(':checked')) {
            $('#J_checkCode').removeClass('displayNone');
            info.removeClass('displayNone');
            submitBtn.text('提交分期订单');

        } else {
            $('#J_checkCode').addClass('displayNone');
            info.addClass('displayNone');
            submitBtn.html('下&nbsp;&nbsp;单');
        }
    });

    function installmentFunc(total) {
        var num = +total;
        info.find('.js-total').text(num * 1.19);
        info.find('.js-installment').text((num / 24).toFixed(2));
    }

    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1([34578])\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");

    var $showGoodsList = $('#J_showGoodsList');

    $('#J_form').validate({
        ignore: "",
        rules: {
            name: "required",
            address: 'required',
            fullAddress: 'required',
            mobile: {
                required: true,
                isPhone: true
            },
            isAgree: "required"
        },
        messages: {
            name: "请填写客户姓名",
            mobile: {
                required: "请填写手机号码"
            },
            address: "请选择地址",
            fullAddress: "请填写详细地址",
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
            if ($showGoodsList.children().length > 0) {
                $.showLoading('订单提交中');
                submitBtn.prop('disabled', true);
                // form.submit();
                submitOrder($('#J_form').serializeObject());
            } else {
                $.toptip('商品列表不能为空', 1000);
            }

        }
    });

    //订单提交
    function submitOrder(data) {
        $.showLoading('订单提交中');
        $.ajax('/wechatOrder', {
            method: 'POST',
            data: data,
            dataType: 'json',
            success: function (data) {
                $.hideLoading();
                if (data.resultCode === 401) {
                    $.toptip(data.resultMsg);
                    resetGoodsList(data.data);
                    return false;
                }
                if (data.resultCode === 402) {
                    $.toptip('按揭码或者身份证号码无效');
                    return false;
                }
                if (data.resultCode !== 200) {
                    $.toptip("订单提交失败，请重试");
                    return false;
                }
                var orderPKId = data.data.id;
                var channelId = data.data.channelId;
                var installmentHuabai = data.data.installmentHuabai;
                var idNumber = data.data.idNumber;
                var authorising = data.data.authorising;
                //提交成功后的跳转
                var payOrderHref = "wechatOrderPay.html"
                    + "?orderPKId=" + orderPKId
                    + "&installmentHuabai=" + installmentHuabai;
                if (!!channelId)
                    payOrderHref = payOrderHref
                        + "&channelId=" + channelId;
                if (!!idNumber)
                    payOrderHref = payOrderHref
                        + "&idNumber=" + idNumber;
                if (!!authorising)
                    payOrderHref = payOrderHref
                        + "&authorising=" + authorising;
                window.location.href = payOrderHref;
            },
            error: function () {
                $.hideLoading();
                $.toptip("系统错误");
            }
        })
    }

    // 发票相关
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
        if (flag) {
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
                $(this).val($('#J_invoiceForm').find('input[name="' + name + '"]').val()).prop('disabled', false);
            });
        },
        setCompany: function () {
            $('#J_needInvoice').html($('#J_invoiceForm').find('.js-company').val());
        }
    };


    $.fn.serializeObject = function () {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function () {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    }

});