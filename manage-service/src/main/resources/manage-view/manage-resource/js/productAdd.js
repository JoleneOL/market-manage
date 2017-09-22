/**
 * Created by Neo on 2017/7/4.
 */
$(function () {
    $(".i-checks").iCheck({
        checkboxClass: "icheckbox_square-green",
        radioClass: "iradio_square-green"
    });

    $('#J_productForm').validate({
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
            //拼接属性及属性值
            var propertyNameValue = "";
            $("input[name^=propertyName],select[name^=propertyName]").each(function(){
                var propertyNameId = $(this).attr('data-propertyNameId');
                var propertyValue = $(this).val();
                if(propertyNameId != undefined){
                    if(propertyNameValue.length > 0){
                        propertyNameValue += ",";
                    }
                    propertyNameValue += (propertyNameId + ":" + propertyValue);
                }
            })
            $("input[name=propertyNameValue]").val(propertyNameValue);
            form.submit();
        }
    });


    $('#J_selectCat').chosen();

    $('#J_datePicker').flatpickr({
        minDate: new Date(),
        locale: 'zh'
    });


    $('#J_addCategory').click(function () {
        layer.prompt({
            title: '添加类目',
            formType: 0
        }, function (pass, index) {
            $.ajax('/products/category/', {
                method: 'post',
                contentType: 'text/plain;charset=UTF-8',
                data: pass,
                success: function () {
                    layer.msg('添加成功');
                    $('#J_selectCat').append('<option value="' + pass + '">' + pass + '</option>')
                        .trigger("chosen:updated");
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                    layer.close(index);
                }
            });
        });
    });

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

    var uploadProductImg = createUploader('#J_uploadProductImg', 'productImg');
    Uploader.init(uploadProductImg, '#J_uploadProductImg');

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
});