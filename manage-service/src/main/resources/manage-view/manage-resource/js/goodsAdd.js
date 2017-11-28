/**
 * Created by helloztt on 2017-09-20.
 */
$(function () {

    $(".i-checks").iCheck({
        checkboxClass: "icheckbox_square-green",
        radioClass: "iradio_square-green"
    });

    $('#J_selectTag').chosen();

    $('#J_addTag').click(function () {
        layer.prompt({
            title: '添加标签',
            formType: 0
        }, function (pass, index) {
            console.log("name:" + pass);
            $.ajax('/manage/addTag', {
                method: 'post',
                data: {name:pass},
                success: function (result) {
                    console.log("result:" + result);
                    if(result == "true"){
                        $('#J_selectTag').append('<option value="' + pass + '">' + pass + '</option>')
                            .trigger("chosen:updated");
                        console.log($("#J_selectTag").html());
                        layer.msg('添加成功');
                    }else{
                        layer.msg("标签已存在");
                    }
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

    var uploadThumbnailImg = createUploader('#J_uploadThumbnailImg','thumbnailImg');
    Uploader.init(uploadThumbnailImg, '#J_uploadThumbnailImg');

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