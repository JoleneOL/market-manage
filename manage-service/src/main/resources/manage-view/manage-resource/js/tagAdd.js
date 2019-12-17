/**
 * Created by Neo on 2017/6/30.
 */
$(function () {
    var dataUrl = $("#J_tagForm").attr('action');
    if($('title').html().indexOf('新增') > -1){
        $('#J_tagForm').validate({
            rules: {
                name: {
                    "required":true,
                    remote: {
                        url: dataUrl + "/check",
                        method: 'get'
                    }
                },
                J_Type: "required"
            },
            messages: {
                name: {
                    "required":"请填写标签名称",
                    "nameUnique":"标签名称已存在"
                },
                J_Type: "请选择标签类型"
            },
            errorElement: "span",
            errorPlacement: function (error, element) {
                error.addClass("help-block");
                if (element.prop("type") === "checkbox") {
                    element.siblings('label').addClass('error');
                } else {
                    error.insertAfter(element);
                }
            },
            highlight: function (element, errorClass, validClass) {
                $(element).parent().addClass("has-error").removeClass("has-success");
            },
            unhighlight: function (element, errorClass, validClass) {
                if ($(element).prop("type") === "checkbox") {
                    $(element).siblings('label').removeClass('error');
                } else {
                    $(element).parent().addClass("has-success").removeClass("has-error");
                }
            },
            submitHandler: function (form) {
                form.submit();
            }
        });
    }

    var Uploader = {
        fileQueued: function (uploader, target) {
            var $wrap = $(target).prev('.js-uploadShow');
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
        },
        uploadSuccess: function (uploader, target) {
            uploader.on('uploadSuccess', function (file, response) {
                $(target).next('input').val(response.id);
                layer.msg('上传成功');
                console.log(file);
                Uploader.successMsg(target);
                uploader.reset();
            });
        },
        uploadError: function (uploader, target) {
            uploader.on('uploadError', function (file) {
                layer.msg('上传失败，重新上传');
                Uploader.errorMsg(target);
                uploader.reset();
            });

            uploader.on('error', function (type) {
                if (type === 'Q_EXCEED_NUM_LIMIT') {
                    layer.msg('超出数量限制');
                    Uploader.errorMsg(target);
                }
                if (type === 'Q_TYPE_DENIED') {
                    layer.msg('文件类型不支持');
                    Uploader.errorMsg(target);
                }
            });
        },
        errorMsg: function (target) {
            // $(target).closest('.weui-cell').addClass('weui-cell_warn');
        },
        successMsg: function (target) {
            // $(target).closest('.weui-cell').removeClass('weui-cell_warn');
        },
        init: function (uploader, target) {
            this.fileQueued(uploader, target);
            this.uploadError(uploader, target);
            this.uploadSuccess(uploader, target);
        }
    };

    var uploadTagImg = createUploader('#J_uploadTagImg', 'iconImg');
    Uploader.init(uploadTagImg, '#J_uploadTagImg');

    function createUploader(id, fileName) {
        return WebUploader.create({
            auto: true,
            swf: '//cdnjs.cloudflare.com/ajax/libs/webuploader/0.1.1/Uploader.swf',
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
});