/**
 * Created by Neo on 2017/7/17.
 */
$(function () {
    var body = $(body);
    WebUploader.create({
        auto: true,
        swf: '//cdn.lmjia.cn/webuploader/0.1.5/Uploader.swf',
        server: body.attr('data-upload-url'),
        pick: {
            id: '#J_uploadFront',
            multiple: false,
            name: 'cardFront'
        },
        accept: {
            title: 'Images',
            extensions: 'jpg,jpeg,png',
            mimeTypes: 'image/jpg,image/jpeg,image/png'
        }
    });
});;