$(function () {
    $('#J_cityPicker, #J_invoiceAddress').cityPicker({
        title: "请选择收货地址",
        onChange: function () {
            $('#J_cityPicker').closest('.weui-cell').removeClass('weui-cell_warn');
        }
    });

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
                form.submit();
            } else {
                $.toptip('商品列表不能为空', 1000);
            }

        }
    });

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


});