/**
 * Created by Neo on 2017/7/10.
 */
$(function () {
    "use strict";

    var table = $('#userTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "/manage/managers",
            "data": function (d) {
                return $.extend({}, d, extendData());
            }
        },
        "ordering": true,
        "lengthChange": false,
        "searching": false,
        "colReorder": true,
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
                "title": "微信号码", "data": "wechatID", "name": "wechatID"
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
                className: 'table-action',
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
        },
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
            $.ajax('/login/' + id + '/password', {
                method: 'put',
                success: function (res) {
                    table.ajax.reload();
                    layer.alert('新密码是：' + res + '，这是最后一次以明文形式显示，系统将不再保留。');
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
            $.ajax('/login/' + id + '/disable', {
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
            $.ajax('/login/' + id + '/enable', {
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
            $.ajax('/login/' + id, {
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