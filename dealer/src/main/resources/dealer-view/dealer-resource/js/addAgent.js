/**
 * Created by Chang on 2017/5/3.
 */
$(function () {
    "use strict";

    var body = $('body');

    DatePicker('#beginDate', '#endDate');

    // $('#higherAgent').searchableSelect();
    $('#higherAgent').select2({
        theme: "bootstrap",
        width: null,
        containerCssClass: ':all:',
        placeholder: "作为最顶级代理商",
        allowClear: true,
        language: "zh-CN",
        ajax: {
            url: body.attr('data-search-agent-url'),
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    search: params.term, // search term
                    page: params.page
                };
            },
            processResults: function (data, params) {
                // parse the results into the format expected by Select2
                // since we are using custom formatting functions we do not need to
                // alter the remote JSON data, except to indicate that infinite
                // scrolling can be used
                params.page = params.page || 1;

                return {
                    results: data.items,
                    pagination: {
                        more: (params.page * 30) < data.total_count
                    }
                };
            },
            cache: true
        },
        escapeMarkup: function (markup) {
            return markup;
        }, // let our custom formatter work
        minimumInputLength: 2,
        templateResult: function (x) {
            if (x.id == '')
                return x.text;
            // 渲染html宽体
            // console.log('templateResult', x);
            return '<b>' + x.rank + '</b>';
        },
        templateSelection: function (x) {
            if (x.id == '')
                return x.text;
            // 渲染当前选择
            return x.rank;
        }

    });

    $('#higherAgent').on('select2:select', function (evt) {
        var title = $.agentTitles[evt.params.data.level + 1];
        $('#rankSuffix').text('(' + title + ')');
    });

    $('#rankSuffix').text('(' + $.agentTitles[0] + ')');
    $('#higherAgent').on('select2:unselect', function (evt) {
        $('#rankSuffix').text('(' + $.agentTitles[0] + ')');
    });

    $("#referrerPhone").makeRecommendSelect();
    // var x = $('#referrerPhone').searchableSelect({
    //     afterSelectItem:function (x) {
    //         console.log('select ',x);
    //     }
    // });
    // console.log(x);
    $('#J_customLevel').click(function () {
        var box = $('#J_customLevelBox');
        if ($(this).is(':checked')) {
            box.show()
                .find('input').prop('disabled', false);
            $('input[name="levelTitle"]').rules('add', {
                required: true,
                messages : {
                    required : "填写自定义代理等级"
                }
            });
        } else {
            box.hide()
                .find('input').prop('disabled', true);
            $('input[name="levelTitle"]').rules('remove');
        }
    });

    var uploaderFront = createUploader('#J_uploadFront', 'cardFront');
    var uploaderBack = createUploader('#J_uploadBack', 'cardBack');
    var uploadLicense = createUploader('#J_uploadLicense', 'businessLicense');

    uploadMakeThumb(uploaderFront, '.js-uploadFront');
    uploadMakeThumb(uploaderBack, '.js-uploadBack');
    uploadMakeThumb(uploadLicense, '.js-uploadLicense');
    uploadError(uploaderFront, '.js-uploadFront');
    uploadError(uploaderBack, '.js-uploadBack');
    uploadError(uploadLicense, '.js-uploadLicense');
    uploadSuccess(uploaderFront, uploaderBack, uploadLicense);


    function createUploader(id, fileName) {
        return WebUploader.create({
            auto: true,
            swf: '//cdnjs.cloudflare.com/ajax/libs/webuploader/0.1.1/Uploader.swf',
            server: body.attr('data-upload-url'),
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

    function uploadSuccess(uploader1, uploader2, uploader3) {
        function successForEach(uploader, name) {
            uploader.on('uploadSuccess', function (file, response) {
                layer.msg('上传成功');
                uploadSuccessMsg(name);
                $('[name=' + name + ']').val(response.id);
                uploader.reset();
            });
        }

        successForEach(uploader1, 'cardFrontPath');
        successForEach(uploader2, 'cardBackPath');
        successForEach(uploader3, 'businessLicensePath')
    }

    function uploadSuccessMsg(msg) {
        var $msg = null;
        if(msg === 'cardFrontPath') {
            $msg =  $('.js-uploadFront');
        }
        if(msg === 'cardBackPath') {
            $msg =  $('.js-uploadBack');
        }
        if(msg === 'businessLicensePath') {
            $msg =  $('.js-uploadLicense');
        }
        message('success', $msg, '上传成功');
    }

    function uploadError(uploader, msg) {
        var $msg = $(msg);

        uploader.on('uploadError', function (file) {
            layer.msg('上传失败，重新上传');
            message('error', $msg, '上传失败，重新上传');
            uploader.reset();
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

    //LMJ的密码
    $.validator.addMethod("isPassword", function (value, element) {
        var mobile = /^[a-zA-Z0-9_]{6,12}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的密码");

    // 粗略的手机号正则
    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1([34578])\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");
    // 曲线救国验证 地址是否选择
    $.validator.addMethod("hasCity", function (value, element) {
        var val = $('#J_cityPicker').val();
        return val.split(" ").length === 3;
    }, "请选择完整的地址");
    // 正数

    $.validator.addMethod("isPositive", function (value, element) {
        var score = /^[0-9]*$/;
        return this.optional(element) || (score.test(value));
    }, "请输入大于0的数字");

    $.validator.setDefaults({
        submitHandler: function (form) {
            form.submit();
        }
    });
    $('#J_addAgentForm').validate({
        ignore: "",
        rules: {
            rank: "required",
            agentName: 'required',
            firstPayment: {
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
            password: {
                required: true,
                isPassword: true
            },
            guideUser: {
                required: true
            },
            mobile: {
                required: true,
                isPhone: true,
                remote: {
                    url: body.attr('data-mobile-validation-url'),
                    method: 'get'
                }
            },
            fullAddress: {
                required: true,
                hasCity: true
            },
            cardFrontPath: "required",
            cardBackPath: "required"
        },
        messages: {
            mobile: {
                remote: '该手机号码当前不可用'
            },
            cardFrontPath: "请上传图片",
            cardBackPath: "请上传图片"
        },
        errorElement: "span",
        errorPlacement: function (error, element) {
            error.addClass("help-block");
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
        format: "yyyy-m-d",
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
        format: "yyyy-m-d",
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