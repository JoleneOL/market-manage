/**
 * 加入最大值和最小值的检查
 * 将比例设置到element中然后让JS去获取
 * Created by Neo on 2017/5/10.
 */
$(function () {

    $('input[type=checkbox][data-group]').change(function () {
        // 如果当前被选中，那么另一个必须不被选中
        var self = $(this);
        if (self.is(':checked')) {
            var group = self.data('group');
            var theOther = $('input[type=checkbox][data-group=' + group + ']').not('[name=' + self.attr('name') + ']');
            theOther.prop('checked', false);

            if (self.data('group') === 'logisticsType') {
                var hideIt = self.attr('name') === 'logisticsTypeSelf';

                var cells = $('._logisticsTypeDelivery');
                if (hideIt) {
                    cells.hide();
                    logisticsNotRequired();
                } else {
                    cells.show();
                    logisticsRequired();
                }
            }
        }
    });


    var amount = $('#J_withdrawAmount');
    var allInInput = $('#J_allInVal');
    var allInBtn = $('#J_allInBtn');
    var rate = $('#J_noInvoiceCost').attr('data-value');
    $('#J_Bank').on('keyup mouseout input', function () {
        var $this = $(this);
        var v = $this.val();
        /\S{5}/.test(v) && $this.val(v.replace(/\s/g, '').replace(/(.{4})/g, "$1 "));
        $('input[name="account"]').val($this.val().replace(/\s/g, ''));
    });
    allInInput.on('keyup mouseout input', function () {
        var $this = $(this);
        var v = $this.val();
        if (v) {
            $this.addClass('view-input-big');
        } else {
            $this.removeClass('view-input-big');
        }
        calculateWithdrawAmount();
    });

    $('input[name=invoice]').click(calculateWithdrawAmount);

    allInBtn.click(function () {
        allInInput
            .val(+$(this).attr('data-all-in'))
            .addClass('view-input-big');

        calculateWithdrawAmount();
    });

    var $invoice = $('#J_extra');

    function logisticsRequired() {
        // $('input[name="logisticsCode"]').rules('add', {
        //     required: true,
        //     messages: {
        //         required: "填写物流单号"
        //     }
        // });
        // $('input[name="logisticsCompany"]').rules('add', {
        //     required: true,
        //     messages: {
        //         required: "填写物流公司"
        //     }
        // });
    }

    function logisticsNotRequired() {
        // $('input[name="logisticsCode"]').rules('remove');
        // $('input[name="logisticsCompany"]').rules('remove');
    }

    $('.js-invoice').change(function () {
        if ($(this).val() === 'true') {
            $invoice.removeClass('displayNone');
            logisticsRequired();
        } else {
            $invoice.addClass('displayNone');
            logisticsNotRequired();
        }
        calculateWithdrawAmount();
    });

    var _body = $('body');

    function calculateWithdrawAmount() {
        var withdrawAmount = +allInInput.val();
        if (!!withdrawAmount) {
            var invoiceType = $('.js-invoice:checked').val();
            //有发票
            if (invoiceType === 'true') {
                amount.text(withdrawAmount);
            } else {
                amount.text((withdrawAmount - withdrawAmount * rate).toFixed(2));
            }

            $('#withSubmit').attr("disabled", false);
            $('#minError').hide();
            $('#maxError').hide();

            // 最大值和最小值检查
            if (withdrawAmount < _body.data('min-amount')) {
                minErrorShow();
                return;
            }
            // 如果选择了发票不再检查了
            if ($("#J_haveInvoice").prop('checked')) {
                return;
            }

            if (withdrawAmount > _body.data('max-amount-without-invoice')) {
                maxErrorShow();
            }
        }


    }

    // 安卓按钮高度
    $(window).resize(function () {
        var h = $(window).height();
        var ua = window.navigator.userAgent.toLowerCase();

        if (/android/i.test(ua)) {
            if (h <= window.screen.availHeight / 2) {
                $('#J_footBtn').hide();
            } else {
                $('#J_footBtn').show();
            }
        }
    });


    // 粗略的手机号正则
    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1([34578])\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");

    // 整正数
    $.validator.addMethod("isPositive", function (value, element) {
        var score = /^[0-9]*$/;
        return this.optional(element) || (score.test(value));
    }, "请输入整正数");
    // 最多两位小数
    $.validator.addMethod("isFloat2", function (value, element) {
        var score = /^[0-9]+\.?[0-9]{0,2}$/;
        return this.optional(element) || (score.test(value));
    }, "最多可输入两位小数");


    $.validator.setDefaults({
        submitHandler: function (form) {
            form.submit();
        }
    });

    $('#J_form').validate({
        ignore: "",
        rules: {
            payee: "required",
            account: {
                required: true,
                number: true,
                isPositive: true
            },
            bank: 'required',
            mobile: {
                required: true,
                isPhone: true
            },
            withdraw: {
                required: true,
                number: true,
                min: 0.01,
                max: +allInBtn.attr('data-all-in'),
                isFloat2: true
            },
            isAgree:'required',
            logisticsCode: {
                required: function () {
                    return $("#logisticsTypeDelivery").is(':checked') && $('#J_haveInvoice').is(':checked');
                }
                // required:'#logisticsTypeDelivery:checked and #J_haveInvoice:checked'
            },
            logisticsCompany: {
                required: function () {
                    return $("#logisticsTypeDelivery").is(':checked') && $('#J_haveInvoice').is(':checked');
                }
            }
        },
        messages: {
            payee: "请填写收款人",
            account: {
                required: "请填写收款账号",
                number: "请输入正确的收款账号",
                digits: "请输入正确的收款账号"
            },
            bank: "请填写开户行",
            mobile: {
                required: "请填写手机号码"
            },
            withdraw: {
                required: "请填写提款金额",
                number: "请填写正确金额",
                min: "提款最小金额为 {0}",
                max: "提款最大金额为 {0}"
            },
            isAgree:"请阅读提现规则",
            logisticsCode: '填写物流单号',
            logisticsCompany: '填写物流公司'
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

    function minErrorShow() {
        $('#minError').show();
        $('#maxError').hide();
        $('#withSubmit').attr("disabled", true);
    }

    function maxErrorShow() {
        $('#maxError').show();
        $('#minError').hide();
        $('#withSubmit').attr("disabled", true);
    }

    var $agreeButton = $('#J_agree_button');
    var $agree = $('#weuiAgree');
    $agreeButton.click(function(){
        $agree.prop('checked','checked');
        localStorage.setItem('agree',true);
    });
    var $rejectButton = $('#J_reject_button');
    $rejectButton.click(function(){
        $agree.prop('checked','');
        localStorage.removeItem('agree')
    })

    var flag = localStorage.getItem('agree');
    if(flag != undefined){
        $agree.prop('checked','checked');
    }
    $agree.click(function(){
        if(!$agree.prop('checked')){
            localStorage.removeItem('agree');
        }else{
            localStorage.setItem('agree',true);
        }
    })
});