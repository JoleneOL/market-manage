/**
 * Created by helloztt on 2017-09-20.
 */
$(function () {


    $('#J_selectTag').chosen();

    $('#J_addTag').click(function () {
        layer.prompt({
            title: '添加标签',
            formType: 0
        }, function (pass, index) {
            $.ajax('/manage/addTag', {
                method: 'post',
                contentType: 'text/plain;charset=UTF-8',
                data: pass,
                success: function (result) {
                    if(result == "true"){
                        layer.msg('添加成功');
                        $('#J_selectTag').append('<option value="' + pass + '">' + pass + '</option>')
                            .trigger("chosen:updated");
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
});