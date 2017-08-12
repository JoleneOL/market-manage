/**
 * Created by Neo on 2017/7/5.
 */
$(function () {
    var license = $('#J_license');
    var updateCost = $('#J_updateCost');
    var submit = $('#J_submit');
    var input = $('input[name="businessLicensePath"]');
    var radio = $('input[name="upgradeMode"]');
    var check = $('input[name="upgradeMode"]:checked');
    var updateLevel = $('#J_updateLevel');
    var updateName = $('#J_updateName');


    $('#J_cityPicker').cityPicker({
        title: "请选择公司地址",
        onChange: function () {
            $('#J_cityPicker').closest('.weui-cell').removeClass('weui-cell_warn');
        }
    });


    updateLevel.change(function () {
        if ($(this).val() === '4') {
            license.find('.extra-badge').hide();
            updateCost.show();
            submit.text('支  付');
            input.closest('.weui-cell').removeClass('weui-cell_warn');
            input.rules('remove');
            if (check.val() !== radio.val()) {
                radio.val(1);
            }
        }

        // if ($(this).val() === '3') {
        //     license.find('.extra-badge').hide();
        //     updateCost.hide();
        //     submit.text('申  请');
        //     input.closest('.weui-cell').removeClass('weui-cell_warn');
        //     input.rules('remove');
        //     if (check.val() === 1) {
        //         radio.val(2);
        //     }
        // }
        if ($(this).val() === '2' || $(this).val() === '3') {
            license.find('.extra-badge').show();
            updateCost.hide();
            submit.text('申  请');
            input.rules('add', {
                required: true,
                messages: {
                    required: "请上传营业执照"
                }
            });
            if (check.val() === 1) {
                radio.val(2);
            }
        }

        updateName.text($(this).find('option:selected').text());

        if ($(this).find('option:selected').val() === '4') {
            $('#J_subText').show();
        } else {
            $('#J_subText').hide();
        }
    });

    radio.change(function () {
        if ($(this).val() === '1') {
            $('#J_paymentBox').show();
            submit.text('支  付');
        } else {
            $('#J_paymentBox').hide();
            submit.text('申  请');
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
                uploader.reset();
            });
        },
        uploadError: function (uploader, target) {
            uploader.on('uploadError', function (file) {
                $.toptip('上传失败，重新上传');
                Uploader.errorMsg(target);
                uploader.reset();
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
            agentName: 'required',
            address: 'required',
            fullAddress: 'required',
            cardFrontPath: "required",
            cardBackPath: "required"
        },
        messages: {
            agentName: '请输入必填字段',
            address: "请选择地址",
            fullAddress: "请填写详细地址",
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

    updateLevel.trigger('change');
});