/**
 * Created by Chang on 2017/5/7.
 */
$(function () {

    var body = $('body');

    if (window.document.location.search.indexOf('InvalidAuthorisingException') >= 0) {
        $.toptip('按揭码或者身份证号码无效');
    }

    $('#J_cityPicker, #J_invoiceAddress').cityPicker({
        title: "请选择收货地址",
        onChange: function () {
            $('#J_cityPicker').closest('.weui-cell').removeClass('weui-cell_warn');
        }
    });

    // 待删除
    // $('#J_goodsAmount').keyup(function () {
    //     countTotal();
    // });
    //
    // $('#J_goodsType').change(function () {
    //     changeAllMoney($(this));
    //     countTotal();
    // });
    //
    //
    // function countTotal() {
    //     var orderTotal = $('#J_orderTotal');
    //     var price = +$('#J_userCost').find('span').eq(0).text();
    //     var cost = +$('#J_serviceCharge').find('span').eq(0).text();
    //     var channel = +orderTotal.attr('data-price-channel');
    //     var amout = +$('#J_goodsAmount').val();
    //     var total = (price + channel + cost) * amout;
    //
    //
    //     orderTotal.find('strong').text(total);
    //     installmentFunc(total);
    //     $('input[name="orderTotal"]').val(total);
    // }
    //
    // function changeAllMoney($ele) {
    //     var $type = $ele.find('option:checked');
    //     // var deposit = $type.attr('data-deposit');
    //     // var isNeed = $type.attr('data-need-install');
    //     // var cost = $type.attr('data-day-cost');
    //
    //     var price = $type.attr('data-price');
    //     // 渠道溢价或者优惠，就保存在总价那个dom中 如果以后有显示该费用的需求，再放到别的地方去
    //     var channel = $type.attr('data-price-channel');
    //     $('#J_orderTotal').attr('data-price-channel', channel);
    //     var model = $type.attr('data-model');
    //     var serviceCharge = $type.attr('data-service-charge');
    //
    //
    //     var fee = serviceCharge ? parseInt(serviceCharge) : 0;
    //
    //     if (fee > 0) {
    //         $('.js-service').removeClass('displayNone');
    //         $('#J_serviceCharge').find('span').eq(0).text(fee);
    //     } else {
    //         $('.js-service').addClass('displayNone');
    //         $('#J_serviceCharge').find('span').eq(0).text(0);
    //     }
    //     $('#J_userCost').find('span').eq(0).text(price);
    //     $('#J_leasedType').val(model);
    //
    //     // $('#J_userDeposit').find('span').eq(0).text(deposit);
    //     // $('#J_package').val('服务费 ' + cost + '元 / 天');
    // }
    // 待删除

    var installment = $('#J_installment');
    var submitBtn = $('#J_submitBtn');
    var info = $('#J_installmentInfo');
    installment.change(function () {
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

    // 粗略的手机号正则
    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1([34578])\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");


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
            amount: {
                required: true
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


    var $goodListData = $('#J_goodsList').find('.js-goods-list');
    var $goodsListArea = $('#J_goodsListArea');

    function getBuyData() {
        var dataJSON = [];
        $goodListData.each(function () {
            var data = {};
            if ($(this).attr('data-amount') > 0) {
                data['model'] = $(this).attr('data-model');
                data['goods'] = $(this).attr('data-goods');
                data['price'] = $(this).attr('data-price');
                data['amount'] = $(this).attr('data-amount');
                dataJSON.push(data);
            }
        });
        return dataJSON;
    }


    function setButData(array) {
        $goodsListArea.empty();
        $.each(array, function (i, v) {
            var hiddenInput = $('<input type="hidden" name="goods[]">').val(v['model']+','+v['amount']);
            $goodsListArea.append(hiddenInput);
        });
    }

    var slideout = new Slideout({
        'panel': document.getElementById('J_main'),
        'menu': document.getElementById('J_goodsList'),
        'padding': 280,
        'tolerance': 70,
        'touch': false,
        'side': 'right'
    });

    $('#J_addGoods').click(function () {
        slideout.open();
    });

    $('#J_goodsCancel').click(closeMenu);


    $('#J_goodsOK').click(function (e) {
        var dataJSON = getBuyData();
        if (dataJSON.length > 0) setButData(dataJSON);
        closeMenu(e);
    });

    function closeMenu(eve) {
        eve.preventDefault();
        slideout.close();
    }

    slideout
        .on('beforeopen', function () {
            this.panel.classList.add('panel-open');
            this.menu.classList.add('open');
        })
        .on('open', function () {
            this.panel.addEventListener('click', closeMenu);
        })
        .on('beforeclose', function () {
            this.panel.classList.remove('panel-open');
            this.panel.removeEventListener('click', closeMenu);
        });

    var Spinner = {
        $parent: $('.spinner'),
        decrease: function () {
            var root = this.$parent;
            root.find('.decrease').click(function () {
                var input = $(this).next('.value');
                var siblings = $(this).siblings('.increase');
                var min = input.attr('min') || 0;
                var val = +input.val();
                if (val < +min) return false;
                input.val(val - 1);
                input.trigger('change');

                if (siblings.prop('disabled') === true)
                    siblings.removeClass('disabled').prop('disabled', false);
                if (+input.val() === +min)
                    $(this).addClass('disabled').prop('disabled', true);

            });
        },
        increase: function () {
            var root = this.$parent;
            root.find('.increase').click(function () {
                var input = $(this).prev('.value');
                var siblings = $(this).siblings('.decrease');
                var max = input.attr('max') || 9999;
                var val = +input.val();
                if (val > +max) return false;
                input.val(val + 1);
                input.trigger('change');

                if (siblings.prop('disabled') === true)
                    siblings.removeClass('disabled').prop('disabled', false);
                if (+input.val() === +max)
                    $(this).addClass('disabled').prop('disabled', true);

            });
        },
        validate: function (ele, val) {
            var min = ele.attr('min') || 0;
            var max = ele.attr('max') || 9999;
            if (val < +min) return -1;
            if (val > +max) return 1;
            return 0;
        },
        change: function () {
            var self = this;
            var root = self.$parent;
            root.find('.value').change(function () {
                var val = +$(this).val();
                var min = $(this).attr('min') || 0;
                var max = $(this).attr('max') || 9999;
                var valid = self.validate($(this), val);
                var parent = $(this).closest('.js-goods-list');
                if (valid !== 0) val = (valid === -1) ? function () {
                    $(this).prev('button').addClass('disabled').prop('disabled', true);
                    $(this).next('button').removeClass('disabled').prop('disabled', false);
                    return min;
                } : function () {
                    $(this).prev('button').removeClass('disabled').prop('disabled', false);
                    $(this).next('button').addClass('disabled').prop('disabled', true);
                    return max;
                };
                $(this).val(val);
                var flag = parent.attr('data-amount') < val;
                parent.attr('data-amount', val);
                countTotal(this, flag);
            });
        },
        init: function () {
            this.decrease();
            this.increase();
            this.change();
        }
    };

    var goodsTotal = $('#J_goodsTotal');

    function countTotal(ele, flag) {
        var nowTotal = Number(goodsTotal.text());
        var parent = $(ele).closest('.js-goods-list');
        var price = Number(parent.attr('data-price'));
        if (flag)
            nowTotal += price;
        else
            nowTotal -= price;
        goodsTotal.text(nowTotal.toFixed(2));
    }

    Spinner.init();

});