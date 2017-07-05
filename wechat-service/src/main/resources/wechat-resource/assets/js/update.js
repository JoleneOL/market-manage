/**
 * Created by Neo on 2017/7/5.
 */
$(function () {
    var license = $('#J_license');
    var updateCost = $('#J_updateCost');
    var submit = $('#J_submit');
    var input = $('input[name="businessLicensePath"]');
    $('#J_updateLevel').change(function () {
        if ($(this).val() === '1') {
            license.find('.extra-badge').hide();
            updateCost.show();
            submit.text('支  付');
            input.closest('.weui-cell').removeClass('weui-cell_warn');
            input.rules('remove');
        }

        if ($(this).val() === '2') {
            license.find('.extra-badge').show();
            updateCost.hide();
            submit.text('申  请');
            input.rules('add', {
                required: true,
                messages : {
                    required : "填写物流单号"
                }
            });
        }
    });


    var Uploader = {
        fileQueued: function (uploader, target) {
            var $wrap = $(target).prev('.weui-uploader__files');

            uploader.on('fileQueued', function (file) {
                var $li = $('<li class="weui-uploader__file"></li>');
                uploader.makeThumb(file, function (error, src) {
                    if (error) {
                        return;
                    }
                    $li.css('background-image', 'url(' + src + ')');
                    $wrap.html($li);
                });
            });
        },
        uploadSuccess: function (uploader, target) {
            uploader.on('uploadSuccess', function (file, response) {
                $(target).next('input').val(response.id);
                $.toast('上传成功');
                Uploader.successMsg(target);
            });
        },
        uploadError: function (uploader, target) {
            uploader.on('uploadError', function (file) {
                $.toptip('上传失败，重新上传');
                Uploader.errorMsg(target);
            });

            uploader.on('error', function (type) {
                if (type === 'Q_EXCEED_NUM_LIMIT') {
                    $.toptip('超出数量限制');
                    Uploader.errorMsg(target);
                }
                if (type === 'Q_TYPE_DENIED') {
                    $.toptip('文件类型不支持');
                    Uploader.errorMsg(target);
                }
            });
        },
        errorMsg: function (target) {
            $(target).closest('.weui-cell').addClass('weui-cell_warn');
        },
        successMsg: function (target) {
            $(target).closest('.weui-cell').removeClass('weui-cell_warn');
        },
        init: function (uploader, target) {
            this.fileQueued(uploader, target);
            this.uploadError(uploader, target);
            this.uploadSuccess(uploader, target);
        }
    };

    var uploaderFront = createUploader('#J_uploadFront', 'cardFront');
    var uploaderBack = createUploader('#J_uploadBack', 'cardBack');
    var uploadLicense = createUploader('#J_uploadLicense', 'businessLicense');

    Uploader.init(uploaderFront, '#J_uploadFront');
    Uploader.init(uploaderBack, '#J_uploadBack');
    Uploader.init(uploadLicense, '#J_uploadLicense');

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


    $('#J_updateForm').validate({
        ignore: '',
        rules: {
            address: "required",
            cardFrontPath: "required",
            cardBackPath: "required"
        },
        messages: {
            address: "请填写公司地址",
            cardFrontPath: "请上传图片",
            cardBackPath: "请上传图片"
        },
        errorPlacement: function (error, element) {
            $.toptip(error);
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
});