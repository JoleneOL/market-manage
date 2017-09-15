/**
 *
 * Created by CJ on 2017/7/10.
 */
$(function () {
    "use strict";

    var table = $('#salesmanTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "/manage/salesmen",
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
                "title": "名字", "data": "name", "name": "name"
            },
            {
                "title": "手机", "data": "mobile", "name": "mobile"
            },
            {
                "title": "状态", "data": "enableLabel", "name": "enableLabel"
            },
            {
                "title": "奖励比例", "data": "rateLabel", "name": "rateLabel"
            },
            {
                "title": "等级", "data": "rank", "name": "rank"
            },
            {
                "title": "操作",
                "className": 'table-action',
                "orderable": false,
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-rate" data-id="' + item.id + '" data-rate-label="' + item.rateLabel + '" data-rate="' + item.rate + '"><i class="fa fa-edit"></i>&nbsp;调整比例</a>';
                    a += '<a href="javascript:;" class="js-rank" data-id="' + item.id + '" data-value="' + item.rank + '"><i class="fa fa-repeat"></i>&nbsp;调整等级</a>';
                    if (item.enable) {
                        a += '<a href="javascript:;" class="js-disableUser" data-id="' + item.id + '" ><i class="fa fa-lock"></i>&nbsp;禁用</a>';
                    }
                    else
                        a += '<a href="javascript:;" class="js-enableUser" data-id="' + item.id + '"><i class="fa fa-unlock"></i>&nbsp;启用</a>';
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

    var newLoginInput = $("#loginInput");
    newLoginInput.makeRecommendSelect();

    $('#addButton').click(function () {
        var target = newLoginInput.val();
        if (!target || target.length === 0) {
            layer.msg('请选择要添为销售的用户');
            return;
        }
        $.ajax('/manage/salesmen', {
            method: 'post',
            contentType: 'text/plain',
            success: function () {
                table.ajax.reload();
            }
        });
    });

    $(document).on('click', '.js-search', function () {
        table.ajax.reload();
    }).on('click', '.js-rate', function () {
        var targetRegion = $('#J_Rate');
        var id = $(this).data('id');
        var current = $(this).data('rate');
        var currentLabel = $(this).data('rate-label');
        layer.open({
            content: targetRegion.html(),
            area: ['200px', '210px'],
            // btn: ['确认', '取消'],
            zIndex: 9999,
            success: function (layerUi) {
                var rangeInput = $('input[type=range]', layerUi);
                rangeInput.change(function () {
                    $('#rateLabel').text(rangeInput.val() + '%');
                });
                rangeInput.val(current);
                $('#rateLabel').text(currentLabel);
            }, yes: function (index) {
                var value = $('input[type=range]').val();
                if (value) {
                    $.ajax('/manage/salesmen/' + id + '/rate', {
                        method: 'put',
                        contentType: 'text/plain',
                        data: '' + value / 100,
                        success: function () {
                            table.ajax.reload();
                            layer.close(index);
                        }
                    });
                }
            }
        });
    }).on('click', '.js-rank', function () {
        var targetRegion = $('#J_Rank');
        var id = $(this).data('id');
        var current = $(this).data('value');
        layer.open({
            content: targetRegion.html(),
            area: ['200px', '180px'],
            // btn: ['确认', '取消'],
            zIndex: 9999,
            success: function (layerUi) {
                var rangeInput = $('input[type=text]', layerUi);
                rangeInput.val(current);
            }, yes: function (index) {
                var value = $('input[name=rank]').val();
                if (value) {
                    $.ajax('/manage/salesmen/' + id + '/rank', {
                        method: 'put',
                        contentType: 'text/plain',
                        data: value,
                        success: function () {
                            table.ajax.reload();
                            layer.close(index);
                        }
                    });
                }
            }
        });
    }).on('click', '.js-disableUser', function () {
        var id = $(this).data('id');
        layer.confirm('确定禁用该用户？', {
            btn: ['确定', '取消']
        }, function (index) {
            $.ajax('/manage/salesmen/' + id + '/disable', {
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
            $.ajax('/manage/salesmen/' + id + '/enable', {
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