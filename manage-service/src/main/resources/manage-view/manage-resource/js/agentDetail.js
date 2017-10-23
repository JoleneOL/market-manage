$(function () {
    var agentId = $('body').data('agent-id');

    $('.js-uploadShow').on('click', 'img', function () {
        var $img = $('<img class="img-feedback-big img-thumbnail"/>').attr('src', $(this).attr('src'));
        var content = $('<div class="container">').append($img);
        layer.open({
            type: 1,
            shade: 0.5,
            title: false,
            shadeClose: true,
            area: ['auto', 'auto'],
            content: content.html()
        });
    });

    $('#J_modifyName').click(function () {
        var name = $(this).prev();
        layer.prompt({title: '输入新用户名，并确认'}, function (value, index) {
            var loading = layer.load();
            $.ajax('/agent/name/' + agentId, {
                method: 'PUT',
                data: {
                    newName: value
                },
                dataType: 'json',
                success: function (res) {
                    layer.close(loading);
                    if (res.resultCode !== 200) {
                        layer.close(index);
                        layer.msg('修改失败，稍后再试');
                        return '';
                    }
                    $('#J_oldName').text(name.text());
                    name.text(value);
                    layer.close(index);
                    layer.msg('修改成功');
                },
                error: function () {
                    layer.close(loading);
                    layer.close(index);
                    layer.msg('修改失败，稍后再试');
                }
            });
        });
    });

    $('#J_modifySuperior').click(function () {
        layer.confirm('<strong class="text-danger">只有一次修改机会！</strong>', {
            icon: 3,
            title: '重要提示',
            btn: ['确定', '取消']
        }, function () {
            layer.msg('的确很重要', {icon: 1});
        });
    });
});