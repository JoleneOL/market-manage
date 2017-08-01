$(function () {
    $('#J_installDate').flatpickr({
        maxDate: new Date(),
        locale: 'zh'
    });

    var uploader = WebUploader.create({
        auto: true,
        swf: '//cdn.lmjia.cn/webuploader/0.1.5/Uploader.swf',
        server: $('body').attr('data-upload-url'),
        pick: {
            id: '#J_upload',
            multiple: false,
            name: 'applyFile'
        },
        fileSizeLimit: 3145728 //限制文件大小3M
    });

    uploader.on('uploadSuccess', function (file, response) {
        $('#J_upload').next('input').val(response.id);
        layer.msg('上传成功');
        uploader.reset();
    });

    uploader.on('uploadError', function (file) {
        layer.msg('上传失败，重新上传');
        uploader.reset();
    });

    uploader.on('error', function (type) {
        if (type === 'Q_EXCEED_NUM_LIMIT') {
            layer.msg('超出数量限制');
        }
        if (type === 'Q_TYPE_DENIED') {
            layer.msg('文件类型不支持');
        }
        if (type === 'Q_EXCEED_SIZE_LIMIT') {
            layer.msg('文件最大支持3M');
        }
    });
});