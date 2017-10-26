$(function () {
    var _body = $('body');
    var agentId = _body.data('agent-id');
    var loginId = _body.data('login-id');

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

    $('#J_modifyAgentRank').click(function () {
        var name = $(this).prev();
        layer.prompt({title: '输入新代理商名称，并确认'}, function (value, index) {
            var loading = layer.load();
            $.ajax('/agent/rank/' + agentId, {
                method: 'PUT',
                data: value,
                contentType: 'text/plain;charset=UTF-8',
                dataType: 'json',
                success: function (res) {
                    layer.close(loading);
                    if (res.resultCode !== 200) {
                        layer.close(index);
                        layer.msg('修改失败，稍后再试');
                        return '';
                    }
                    // $('#J_oldName').text(name.text());
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


    $('#J_modifyName').click(function () {
        var name = $(this).prev();
        layer.prompt({title: '输入新名字，并确认'}, function (value, index) {
            var loading = layer.load();
            $.ajax('/login/name/' + loginId, {
                method: 'PUT',
                data: value,
                contentType: 'text/plain;charset=UTF-8',
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
    var superiorInput = $("#superiorInput");
    superiorInput.makeRecommendSelect();

    $('#J_modifySuperior').click(function () {
        var self = $(this);
        layer.confirm('<strong class="text-danger">只有一次修改机会！</strong>', {
            icon: 3,
            title: '重要提示',
            btn: ['确定', '取消']
        }, function (index) {
            self.hide();
            self.next().removeClass('hide');
            layer.close(index);
        });
    });

    $('#J_confirmModify').click(function () {
        var $parent = $(this).parent();
        var $form = $(this).closest('.form-horizontal');
        var newSuperiorId = superiorInput.val();
        if (!newSuperiorId) {
            layer.msg('请选择新的上级代理商');
            return;
        }
        var loading = layer.load();
        $.ajax('/agent/superior/' + agentId, {
            method: 'PUT',
            contentType: 'text/plain;charset=UTF-8',
            data: newSuperiorId,
            dataType: 'json',
            success: function (res) {
                layer.close(loading);
                if (res.resultCode !== 200) {
                    layer.close(index);
                    layer.msg('修改失败，稍后再试');
                    return '';
                }
                $('#J_superiorName').text(res.data['name']);
                $('#J_modifySuperior').remove();
                $parent.remove();
                $form.addClass('hide');
                layer.msg('修改成功');
            },
            error: function () {
                layer.close(loading);
                layer.close(index);
                layer.msg('修改失败，稍后再试');
            }
        });
    });

    $('#J_cancelModify').click(function () {
        $(this).closest('.form-horizontal').addClass('hide');
        $('#J_modifySuperior').show();
    });

    var guideInput = $("#guideInput");
    guideInput.makeRecommendSelect(
        {
            placeholder: '请选择一个用户作为新的引导者'
        }
    );

    $('#J_modifyGuide').click(function () {
        var self = $(this);
        layer.confirm('<strong class="text-danger">只有一次修改机会！</strong>', {
            icon: 3,
            title: '重要提示',
            btn: ['确定', '取消']
        }, function (index) {
            self.hide();
            self.next().removeClass('hide');
            layer.close(index);
        });
    });
    $('#J_confirmModifyGuide').click(function () {
        var $parent = $(this).parent();
        var $form = $(this).closest('.form-horizontal');
        var newSuperiorId = guideInput.val();
        if (!newSuperiorId) {
            layer.msg('请选择新的引导者');
            return;
        }
        var loading = layer.load();
        $.ajax('/login/guide/' + loginId, {
            method: 'PUT',
            contentType: 'text/plain;charset=UTF-8',
            data: newSuperiorId,
            dataType: 'json',
            success: function (res) {
                console.log(res);
                layer.close(loading);
                if (res.resultCode !== 200) {
                    layer.close(index);
                    layer.msg('修改失败，稍后再试');
                    return '';
                }
                $('#J_guideName').text(res.data['name']);
                $('#J_modifyGuide').remove();
                $parent.remove();
                $form.addClass('hide');
                layer.msg('修改成功');
            },
            error: function () {
                layer.close(loading);
                layer.close(index);
                layer.msg('修改失败，稍后再试');
            }
        });
    });
    $('#J_cancelModifyGuide').click(function () {
        $(this).closest('.form-horizontal').addClass('hide');
        $('#J_modifyGuide').show();
    });

    var $mobile = $('input[name="newMobile"]');
    var $authCode = $('input[name="authCode"]');
    var sendAuthCodeUrl = _body.attr('data-url-sendAuthCode');

    $('#J_sendAuthCode').click(function () {
        var self = $(this);
        var mobile = $mobile.val();
        if (!/^1([34578])\d{9}$/.test(mobile)) {
            layer.msg('请输入正确的手机号');
            return;
        }
        var loading = layer.load();
        sendSMS(self);
        $.ajax(sendAuthCodeUrl, {
            method: 'POST',
            data: {
                mobile: mobile
            },
            dataType: 'json',
            success: function (data) {
                layer.close(loading);
                if (data.resultCode !== 200) {
                    layer.msg(data.resultMsg);
                    return false;
                }
            },
            error: function () {
                layer.msg("系统错误");
            }
        })
    });

    function sendSMS(ele) {
        ele.prop('disabled', true);
        var s = 60;
        var t = setInterval(function () {
            ele.text(s-- + 's');
            if (s === -1) {
                clearInterval(t);
                ele.text('获取验证码')
                    .prop('disabled', false);
            }
        }, 1000);
    }

    $('#J_cancelModifyMobile').click(function () {
        $mobile.val('');
        $authCode.val('');
    });
    $('#J_confirmModifyMobile').click(function () {
        if (!$mobile.val()) {
            return layer.msg('手机号不能为空');
        }
        if (!/^1([34578])\d{9}$/.test($mobile.val())) {
            layer.msg('请输入正确的手机号');
            return;
        }
        // if (!$authCode.val()) {
        //     return layer.msg('验证码不能为空');
        // }
        var loading = layer.load();
        $.ajax('/login/mobile/' + loginId, {
            method: 'PUT',
            data: $mobile.val(),
            // ,$authCode: $authCode.val()
            contentType: 'text/plain;charset=UTF-8',
            dataType: 'json',
            success: function (res) {
                layer.close(loading);
                if (res.resultCode !== 200) {
                    layer.msg(res.resultMsg);
                    return '';
                }
                $('#J_mobile').text($mobile.val());
                $('#J_mobileModal').modal('hide');
                $mobile.val('');
                $authCode.val('');
                layer.msg('修改成功');
            },
            error: function () {
                layer.close(loading);
                layer.msg('修改失败，稍后再试');
            }
        });
    });

    var table = $('#subordinateTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": _body.data('subordinate'),
            "data": function (d) {
                return $.extend({}, d, extendData());
            }
        },
        "ordering": true,
        "lengthChange": false,
        "searching": false,
        "colReorder": true,
        "order": [[2, "desc"]],
        "columns": [
            {
                "title": "下级姓名", "data": "name", "name": "name"
            },
            {
                "title": "手机号", "data": "mobile", "name": "mobile"
            },
            {
                "title": "加入时间", "data": "createdTime", "name": "createdTime"
            },
            {
                "title": "首次下单时间", "data": "earliestOrderTime", "name": "earliestOrderTime", "orderable": false
            },
            {
                "title": "总下单金额", "data": "orderTotal", "name": "orderTotal", "orderable": false
            }
        ],
        "displayLength": 15,
        "dom": "<'row'<'col-sm-12'B>>" +
        "<'row'<'col-sm-12'tr>>" +
        "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        "buttons": [{
            "extend": "excel",
            "text": "导出 Excel",
            "className": "btn-xs",
            "exportOptions": {
                "columns": ":not(.table-action)"
            }
        }, {
            "extend": 'colvis',
            "text": "筛选列",
            "className": "btn-xs"
        }]
    });
    $(document).on('click', '.js-search', function () {
        table.ajax.reload();
    });

    // 添加额外的参数
    function extendData() {
        var formItem = $('.js-selectToolbar').find('.form-control');
        if (formItem.length === 0) return {};
        var data = {};

        formItem.each(function () {
            var t = $(this);
            var n = t.attr('name');
            var v = t.val();
            if (v) data[n] = v;
        });
        return data;
    }


    $('#journalTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": _body.data('journal'),
        "ordering": true,
        "lengthChange": false,
        "searching": false,
        "colReorder": true,
        "columns": [
            {
                "title": "日期", "data": "happenTime", "name": "happenTime"
            },
            {
                "title": "变动金额（元）",
                "name": "changedAbsMoney",
                data: function (item) {
                    if (item.event === 'increase')
                        return '<span class="text-danger">&#43; ' + item.changedAbsMoney + '</span>';
                    return '<span class="text-navy">&#45; ' + item.changedAbsMoney + '</span>'
                }
            },
            {
                "title": "概要", "data": "type", "name": "type"
            }
            ,
            // {
            //     "title": "当时余额（元）", "name": "balance",
            //     data: function (item) {
            //         return '<span>￥' + item.balance + '</span>';
            //     }
            // },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    if (item.event === 'increase')
                        return '';
                    return '<a href="javascript:;" class="js-checkInfo" data-id="' + item.orderId + '"><i class="fa fa-check-circle-o"></i>&nbsp;详情</a>';
                }
            }
        ],
        "displayLength": 15,
        "dom": "<'row'<'col-sm-12'B>>" +
        "<'row'<'col-sm-12'tr>>" +
        "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        "buttons": [{
            "extend": "excel",
            "text": "导出 Excel",
            "className": "btn-xs",
            "exportOptions": {
                "columns": ":not(.table-action)"
            }
        }, {
            "extend": 'colvis',
            "text": "筛选列",
            "className": "btn-xs"
        }]
    });

    $(document).on('click', '.js-checkInfo', function () {
        window.location.href = _body.data('good-advance-order-detail') + '?orderId=' + $(this).data('id');
    });
})
;