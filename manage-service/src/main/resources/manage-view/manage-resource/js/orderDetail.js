$(function () {
    $('#J_installDate').flatpickr({
        maxDate: new Date(),
        locale: 'zh'
    });

    var uploader = WebUploader.create({
        auto: true,
        swf: '//cdn.lmjia.cn/webuploader/0.1.5/Uploader.swf',
        server: $('body').data('upload-url'),
        pick: {
            id: '#J_upload',
            multiple: false,
            name: 'applyFilePlugin'
        },
        fileSizeLimit: 3145728, //限制文件大小3M
        accept: {
            title: 'Files',
            extensions: 'doc,docx,xls,xlsx,gif,jpg,jpeg,png',
            mimeTypes: 'image/gif,image/jpg,image/jpeg,image/png,' +
            'application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,' +
            'application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        }
    });

    var $wrap = $('#J_uploadShow');
    uploader.on('uploadSuccess', function (file, response) {
        $('#J_upload').next('input').val(response.id);
        $wrap.append(message.success());
        if (file.type.indexOf('image') > -1) {
            $wrap.append('<img src="'+response.url+'" class="hide">')
        } else {
            $wrap.find('.hide').remove();
        }
        layer.msg('上传成功');
        uploader.reset();
    });

    uploader.on('uploadError', function (file) {
        $wrap.append(message.failure());
        layer.msg('上传失败，重新上传');
        uploader.reset();
    });


    uploader.on('fileQueued', function (file) {
        var $img = $('<img />');
        uploader.makeThumb(file, function (error, src) {
            if (error) {
                $wrap.empty().addClass('img-bg-file');
            } else {
                $img.attr('src', src);
                $wrap.html($img);
            }

        });
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

    var message = {
        success: function () {
            return '<i class="upload-msg msg-success fa fa-check"></i>'
        },
        failure: function () {
            return '<i class="upload-msg msg-failure fa fa-times"></i>'
        }
    };

    $wrap.click(function () {
        var img = $(this).find('.hide');
        if (img.length === 0) return '';
        var $img = $('<img class="img-feedback-big img-thumbnail"/>').attr('src', img.attr('src'));
        var content = $('<div class="container">').append($img);
        layer.open({
            type: 1,
            shade: 0.5,
            title: false,
            shadeClose: true,
            area: ['auto', 'auto'],
            content: content.html()
        });
    })
});