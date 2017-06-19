/**
 * Created by Neo on 2017/5/8.
 */
$(function () {

    if (typeof moment === 'function') {
        var date = moment().format('YYYY-MM-DD');
        $("#J_maintainTime").datetimePicker({
            title: '预约时间',
            min: date
        });
    }

    $('#J_repairType, #J_Reason, #J_Logistics').change(function () {
        var $extra = $(this).closest('.weui-cell_select').next('.J_extra');
        if ($(this).val() === 'other') {
            $extra.show();
            $('textarea[name="otherType"]').rules('add', {
                required: true,
                messages : {
                    required : "填写原因"
                }
            });
        } else {
            $extra.hide();
            $('textarea[name="otherType"]').rules('remove');
        }
    });

    $('#J_Cancel').click(function () {
        $.confirm("您确定取消售后服务么？", "确认取消？", function () {
            $('#J_form').submit();
        }, function () {
            //取消操作
        });
    });

    $.validator.setDefaults({
        submitHandler: function (form) {
            form.submit();
        }
    });


    // 粗略的手机号正则
    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1([34578])\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");

    $('#J_form').validate({
        rules: {
            logisticsCompany: "required",
            logisticsCode: "required"
        },
        messages: {
            logisticsCompany: "请填入物流公司",
            logisticsCode: "请填入物流单号"
        },
        errorPlacement: function (error, element) {
            $.toptip(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').addClass("weui-cell_warn")
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').removeClass("weui-cell_warn");
        }
    });


    $('#J_maintainForm').validate({
        rules: {
            appointment: "required"
        },
        messages: {
            logisticsCompany: "请选择预约时间"
        },
        errorPlacement: function (error, element) {
            $.toptip(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').addClass("weui-cell_warn")
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest('.weui-cell').removeClass("weui-cell_warn");
        }
    });


    $('select[name="maintainType"]').change(function () {
        if( +$(this).val() === 1) {
            $('#J_require').show();
            $('#J_moving').hide();
            $('input[name="appointment"]').rules('add', {
                required: true,
                messages : {
                    required : "请选择预约时间"
                }
            });
            $('input[name="moveAddress"]').rules('remove');
            $('input[name="moveName"]').rules('remove');
            $('input[name="movePhone"]').rules('remove');
        } else {
            $('#J_require').hide();
            $('#J_moving').show();
            $('input[name="appointment"]').rules('remove');
            $('input[name="moveAddress"]').rules('add', {
                required: true,
                messages : {
                    required : "请选择移机地址"
                }
            });
            $('input[name="moveName"]').rules('add', {
                required: true,
                messages : {
                    required : "请填写联系人"
                }
            });
            $('input[name="movePhone"]').rules('add', {
                required: true,
                isPhone: true,
                messages : {
                    required : "请填写联系电话"
                }
            });

        }
    });

    $('#J_moveAddress').cityPicker({
        title: "请选择收货地址",
        onChange: function () {
            $('#J_cityPicker').closest('.weui-cell').removeClass('weui-cell_warn');
        }
    });
});