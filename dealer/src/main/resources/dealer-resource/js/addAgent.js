/**
 * Created by Chang on 2017/5/3.
 */
$(function () {

    var uploaderFront = createUploader('#J_uploadFront', 'cardFront');
    var uploaderBack = createUploader('#J_uploadBack', 'cardBack');


    makeThumb(uploaderFront, '.js-uploadFront');
    makeThumb(uploaderBack, '.js-uploadBack');
    successOrError(uploaderFront);
    successOrError(uploaderBack);


    function createUploader(id, fileName) {
        return WebUploader.create({
            auto: true,
            swf: '//cdn.lmjia.cn/webuploader/0.1.5/Uploader.swf',
            server: '/api/fileUpload',
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
    function makeThumb(uploader, wrapper) {
        var $wrap = $(wrapper);
        uploader.on( 'fileQueued', function( file ) {
            var $img = $('<img />');
            uploader.makeThumb( file, function( error, src ) {
                if ( error ) {
                    return;
                }
                $img.attr( 'src', src );
                $wrap.html($img);
            });
        });
    }
    
    function successOrError(uploader) {
        uploader.on( 'uploadSuccess', function( file ) {
            layer.msg('上传成功');
        });

        uploader.on( 'uploadError', function( file ) {
            layer.msg('上传失败，重新上传');
        });
    }
});