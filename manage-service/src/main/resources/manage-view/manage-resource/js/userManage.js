/**
 * Created by Neo on 2017/7/10.
 */
$(function () {
    "use strict";

    var table = $('#userTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "/user/list",
            "data": function (d) {
                return $.extend({}, d, extendData());
            }
        },
        "ordering": false,
        "lengthChange": false,
        "searching": false,
        "columns": [
            {
                "data": "id", "name": "id"
            },
            {
                "title": "用户名", "data": "name", "name": "name"
            },
            {
                "title": "部门", "data": "department", "name": "department"
            },
            {
                "title": "真实姓名", "data": "realName", "name": "realName"
            },
            {
                "title": "微信账号", "data": "wechatId", "name": "wechatId"
            },
            {
                "title": "角色", "data": "role", "name": "role"
            },
            {
                "title": "状态", "data": "state", "name": "state"
            },
            {
                "title": "备注", "data": "remark", "name": "remark"
            },
            {
                title: "操作",
                className: 'table-operate',
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-edit" data-id="' + item.id + '"><i class="fa fa-edit"></i>&nbsp;编辑</a>';
                    a += '<a href="javascript:;" class="js-resetPassword" data-id="' + item.id + '"><i class="fa fa-repeat"></i>&nbsp;重置密码</a>';
                    if (item.stateCode)
                        a += '<a href="javascript:;" class="js-disableUser" data-id="' + item.id + '" ><i class="fa fa-lock"></i>&nbsp;禁用</a>';
                    else
                        a += '<a href="javascript:;" class="js-enableUser" data-id="' + item.id + '"><i class="fa fa-unlock"></i>&nbsp;启用</a>';
                    a += '<a href="javascript:;" class="js-delete" data-id="' + item.id + '"><i class="fa fa-trash-o"></i>&nbsp;删除</a>';
                    return a;
                }
            }
        ],
        "displayLength": 15,
        "drawCallback": function () {
            clearSearchValue();
        }
    });

    $(document).on('click', '.js-search', function () {
        // 点击搜索方法。但如果数据为空，是否阻止
        table.ajax.reload();
    }).on('click', '.js-edit', function () {
        // TODO
        // 需要获取一些参数供详情跳转
        window.location.href = '_userEdit.html?id=' + $(this).data('id');
    }).on('click', '.js-resetPassword', function () {
        var id = $(this).data('id');
        layer.confirm('确定重置密码？', {
            btn: ['确定', '取消'] //按钮
        }, function (index) {
            $.ajax('/password/' + id + '/reset', {
                method: 'put',
                success: function () {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                }
            });
        });
    }).on('click', '.js-disableUser', function () {
        var id = $(this).data('id');
        layer.confirm('确定禁用该用户？', {
            btn: ['确定', '取消']
        }, function (index) {
            $.ajax('/user/' + id + '/disable', {
                method: 'put',
                success: function () {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                }
            });
        });
    }).on('click', '.js-enableUser', function () {
        var id = $(this).data('id');
        layer.confirm('确定启用该用户？', {
            btn: ['确定', '取消']
        }, function (index) {
            $.ajax('/user/' + id + '/enable', {
                method: 'put',
                success: function () {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                }
            });
        });
    }).on('click', '.js-delete', function () {
        var id = $(this).data('id');
        layer.confirm('确定删除该用户？', {
            btn: ['确定', '取消']
        }, function (index) {
            $.ajax('/user/' + id + '/delete', {
                method: 'delete',
                success: function () {
                    table.ajax.reload();
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                }
            });
        });
    });

    // 添加额外的参数
    function extendData() {
        var formItem = $('.js-selectToolbar').find('.form-control');
        if (formItem.length === 0)  return {};
        var data = {};

        formItem.each(function () {
            var t = $(this);
            var n = t.attr('name');
            var v = t.val();
            if (v) data[n] = v;
        });
        return data;
    }

    function clearSearchValue() {
        //TODO
    }
});