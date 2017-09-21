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
});