/**
 * Created by Chang on 2017/5/3.
 */
$(function () {
    "use strict";

    DatePicker('#beginDate', '#endDate');

    $('#higherAgent').searchableSelect();
    $('#referrerPhone').searchableSelect();

    var uploaderFront = createUploader('#J_uploadFront', 'cardFront');
    var uploaderBack = createUploader('#J_uploadBack', 'cardBack');

    uploadMakeThumb(uploaderFront, '.js-uploadFront');
    uploadMakeThumb(uploaderBack, '.js-uploadBack');
    uploadError(uploaderFront, '.js-uploadFront');
    uploadError(uploaderBack, '.js-uploadBack');
    uploadSuccess(uploaderFront, uploaderBack);


    function createUploader(id, fileName) {
        return WebUploader.create({
            auto: true,
            swf: '//cdn.lmjia.cn/webuploader/0.1.5/Uploader.swf',
            server: $('body').attr('data-upload-url'),
            pick: {
                id: id,
                multiple: false,
                name: fileName
            },
            accept: {
                title: 'Images',
                extensions: 'jpg,jpeg,png',
                mimeTypes: 'image/jpg,image/jpeg,image/png'
            }
        });
    }

    function uploadMakeThumb(uploader, wrapper) {
        var $wrap = $(wrapper).find('.js-uploadShow');
        uploader.on('fileQueued', function (file) {
            var $img = $('<img />');
            uploader.makeThumb(file, function (error, src) {
                if (error) {
                    return;
                }
                $img.attr('src', src);
                $wrap.html($img);
            });
        });
    }

    function uploadSuccess(uploader1, uploader2) {
        // var $msg = $(msg);

        function successForEach(uploader, name) {
            uploader.on('uploadSuccess', function (file, response) {
                layer.msg('上传成功');
                // message('success', $msg, '上传成功');
                // console.log(response);
                $('[name=' + name + ']').val(response.id);
                if (uploader1.getStats().successNum > 0 && uploader2.getStats().successNum > 0)
                    $('#J_submitBtn').prop('disabled', false);
            });
        }

        successForEach(uploader1, 'cardFrontPath');
        successForEach(uploader2, 'cardBackPath');
    }

    function uploadError(uploader, msg) {
        var $msg = $(msg);

        uploader.on('uploadError', function (file) {
            layer.msg('上传失败，重新上传');
            message('error', $msg, '上传失败，重新上传');
        });

        uploader.on('error', function (type) {
            if (type === 'Q_EXCEED_NUM_LIMIT') {
                layer.msg('超出数量限制');
                message('error', $msg, '超出数量限制');
            }
            if (type === 'Q_TYPE_DENIED') {
                layer.msg('文件类型不支持');
                message('error', $msg, '文件类型不支持');
            }
        });
    }

    function message(type, $ele, msg) {
        if (type === 'success') {
            $ele.find('.has-error')
                .addClass('has-success')
                .find('.fa')
                .removeClass('fa-exclamation')
                .addClass('fa-check');
        } else {
            $ele.find('.has-error')
                .removeClass('has-success')
                .find('.fa')
                .removeClass('fa-check')
                .addClass('fa-exclamation');
        }
        $ele.find('strong').text(msg);
    }

    // 粗略的手机号正则
    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1(3|4|5|7|8)\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");
    // 曲线救国验证 地址是否选择
    $.validator.addMethod("hasCity", function (value, element) {
        var val = $('#J_cityPicker').val();
        return val.split("/").length === 3;
    }, "请选择完整的地址");
    // 正数

    $.validator.addMethod("isPositive", function (value, element) {
        var score = /^[0-9]*$/;
        return this.optional(element) || (score.test(value));
    }, "请输入大于0的数字");

    $.validator.setDefaults({
        submitHandler: function (form) {
            form.submit();
            // layer.msg('OK');
        }
    });
    $('#J_addAgentForm').validate({
        rules: {
            higherAgent: "required",
            level: "required",
            agentName: 'required',
            upfrontPayment: {
                required: true,
                number: true,
                isPositive: true
            },
            agencyFee: {
                required: true,
                number: true,
                isPositive: true
            },
            beginDate: "required",
            endDate: "required",
            authCode: {
                required: true
            },
            referrerPhone: {
                required: true,
                isPhone: true
            },
            phone: {
                required: true,
                isPhone: true
            },
            address: {
                required: true
            },
            fullAddress: {
                required: true,
                hasCity: true
            }
        },
        messages: {},
        errorElement: "span",
        errorPlacement: function (error, element) {
            error.addClass("help-block")
            if (element.parent('.input-group').length > 0) {
                $(element).siblings('.input-group-btn').find('.btn').prop('disabled', true);
                error.insertAfter(element.parent());
            } else {
                error.insertAfter(element);
            }
        },
        highlight: function (element, errorClass, validClass) {
            $(element).parent().addClass("has-error").removeClass("has-success");
        },
        unhighlight: function (element, errorClass, validClass) {
            if ($(element).parent('.input-group').length > 0) {
                $(element).siblings('.input-group-btn').find('.btn').prop('disabled', false);
            }
            $(element).parent().addClass("has-success").removeClass("has-error");
        }
    });

});

function DatePicker(beginSelector, endSelector) {
    // 仅选择日期
    $(beginSelector).datetimepicker({
        todayBtn: "linked",
        language: "zh-CN",
        autoclose: true,
        format: "yyyy-mm-dd",
        clearBtn: true,
        todayHighlight: true,
        minView: 2,
        startDate: new Date()
    }).on('changeDate', function (e) {
        var startTime = e.date;
        $(endSelector).datetimepicker('setStartDate', startTime);
    }).on('hide', function (e) {
        e.target.focus();
    });
    $(endSelector).datetimepicker({
        language: "zh-CN",
        autoclose: true,
        format: "yyyy-mm-dd",
        todayHighlight: true,
        clearBtn: true,
        minView: 2,
        startDate: new Date()
    }).on('changeDate', function (e) {
        var endTime = e.date;
        $(beginSelector).datetimepicker('setEndDate', endTime);
    }).on('hide', function (e) {
        e.target.focus();
    });

}