$(function () {
    "use strict";

    $('#J_datePicker').flatpickr({
        maxDate: new Date(),
        locale: 'zh'
    });

    var _body = $('body');

    var logisticsDetailUrl = _body.attr('data-logistics-detail-url');
    var quickUrl = _body.attr('data-quick-url');
    var allowMockPay = _body.attr('data-allow-mock-pay') === 'true';
    var allowSettlement = _body.attr('data-allow-settlement') === 'true';

    var table = $('#orderTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": _body.attr('data-url'),
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
                "title": "订单号",
                "data": "orderId",
                "name": "orderId"
            },
            {
                "title": "合伙人",
                "data": "user",
                "name": "user"
            },
            {
                "title": "合伙人级别",
                "data": "userLevel",
                "name": "userLevel"
            },
            {
                "title": "购买产品",
                "data": "goods",
                "name": "goods"
            },
            {
                "title": "数量",
                "data": "amount",
                "name": "amount"
            },
            {
                "title": "订单用户",
                "data": "orderUser",
                "name": "orderUser"
            },
            {
                "title": "订单地址",
                "data": "address",
                "name": "address"
            },
            {
                "title": "订单手机号",
                "data": "phone",
                "name": "phone"
            },
            {
                "title": "支付方式",
                "data": "method",
                "name": "method"
            },
            {
                "title": "订单总金额",
                "data": "total",
                "name": "total"
            },
            {
                "title": "状态",
                "name": "status",
                "data": function (item) {
                    if (item.methodCode === 0 && item.statusCode === 7) return '<span class="text-danger">' + item.status + '</span>';
                    if (item.methodCode === 2 && item.statusCode === 4) return '<span class="text-danger">' + item.status + '</span>';
                    return item.status
                }
            },
            {
                "title": "下单时间",
                "data": "orderTime",
                "name": "orderTime"
            },
            // {
            //     "title": "操作人",
            //     "data": "operator",
            //     "name": "operator"
            // },
            {
                title: "操作",
                className: 'table-action',
                data: function (item) {
                    var makeLogistics = '<a href="javascript:;" class="js-makeLogistics" data-id="' + item.id + '"><i class="fa fa-truck"></i>&nbsp;物流发货</a>';

                    var viewLogistics = '<a href="javascript:;" class="js-viewLogistics" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;查看物流</a>';

                    var a = '<a href="javascript:;" class="js-checkOrder" data-id="' + item.id + '" data-from="' + item.methodCode + '"><i class="fa fa-check-circle-o"></i>&nbsp;查看</a>';
                    var b = '<a href="javascript:;" class="js-modifyOrder" data-id="' + item.id + '"><i class="fa fa fa-pencil-square-o"></i>&nbsp;修改</a>';
                    var c = '<a href="javascript:;" class="js-quickDone" data-id="' + item.id + '"><i class="fa fa fa-pencil-square-o"></i>&nbsp;快速完成订单</a>';
                    var c2 = '<a href="javascript:;" class="js-quickDoneMore" data-id="' + item.id + '"><i class="fa fa fa-pencil-square-o"></i>&nbsp;快速完成订单</a>';
                    var d = '<a href="javascript:;" class="js-mockPay" data-id="' + item.id + '"><i class="fa fa fa-pencil-square-o"></i>&nbsp;模拟支付</a>';
                    var e = '<a href="javascript:;" class="js-settlement" data-id="' + item.id + '"><i class="fa fa fa-pencil-square-o"></i>&nbsp;重新结算</a>';
                    var f = '<a href="javascript:;" class="btn btn-primary btn-xs js-dispatch" data-id="' + item.id + '">派单</a>';

                    if (item.statusCode === 1 || item.statusCode === 2 || item.statusCode === 8) {
                        // 待付款、待发货、已付款，订单可修改
                        a = a + b;
                    }
                    if (item.statusCode === 1 && allowMockPay) {
                        a = a + d;
                    }
                    if (item.statusCode !== 1 && allowSettlement) {
                        // 已付款及其以上（排除待付款）
                        a = a + e;
                    }
                    if (item.statusCode === 2) {
                        // 物流发货
                        a = a + makeLogistics;
                    } else if (item.statusCode >= 3) {
                        a = a + viewLogistics;
                    }

                    if (item.methodCode === 0) {
                        //全额
                        if (item.quickDoneAble && quickUrl) {
                            a = a + c;
                        }
                    }
                    if (item.methodCode === 2) {
                        //投融家
                        if (item.quickDoneAble && quickUrl) {
                            a = a + c2;
                        }
                    }
                    if (item.methodCode === 3) {
                        //花呗
                    }

                    if (item.statusCode === 7) {
                        a = a + f;
                    }
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
            "className": "btn-success btn-xs",
            "exportOptions": {
                "columns": ":not(.table-action)"
            }
        }, {
            "extend": 'colvis',
            "text": "筛选列",
            "className": "btn-success btn-xs"
        }]
    });

    $._table = table;

    //物流相关
    var makeLogisticsRegion = $('#J_makeLogistics');
    var makeLogisticsRegionDepotId;

    function depotSelectChange(parent) {
        var quantity = $('[name=quantity]', parent);
        var distance = $('[name=distance]', parent);
        var current = +makeLogisticsRegionDepotId.val();
        // 从 $.depots 里寻找匹配的
        $.each($.depots, function (_, data) {
            if (data.id == current) {
                quantity.val(data.quantity);
                distance.val(data.distance);
            }
        });
    }


    function depotsUpdate(depots) {
        $.depots = depots;
        makeLogisticsRegionDepotId.empty();
        $.each($.depots, function (_, data) {
            makeLogisticsRegionDepotId.append('<option value="' + data.id + '">' + data.name + '</option>');
        });
        depotSelectChange();
    }

    //物流相关结束

    var detailForm = $('#detailForm');

    $(document).on('click', '.js-search', function () {
        // 点击搜索方法。但如果数据为空，是否阻止
        table.ajax.reload();
    }).on('click', '.js-checkOrder', function () {
        $('input[name=id]', detailForm).val($(this).attr('data-id'));
        $('input[name=from]', detailForm).val($(this).attr('data-from'));
        detailForm.submit();
    }).on('click', '.js-viewLogistics', function () {
        window.location.href = logisticsDetailUrl + '?id=' + $(this).data('id');
    }).on('click', '.js-makeLogistics', function () {
        var orderId = $(this).attr('data-id');
        $.ajax('/orderData/logistics/' + orderId, {
            dataType: 'json',
            method: 'get',
            success: function (response) {
                // 获取可用仓库
                var depots = response.depots;
                if (depots.length === 0)
                    layer.msg('没有该货品可用的库存，请尽快补充库存。');
                else {
                    layer.open({
                        content: makeLogisticsRegion.html(),
                        area: ['500px', 'auto'],
                        btn: ['确认', '取消'],
                        zIndex: 9999,
                        success: function (layerd) {
                            makeLogisticsRegionDepotId = $('[name=depotId]', layerd);
                            $('[name=depotId]', layerd).change(function () {
                                depotSelectChange(layerd);
                            });
                            $('[name=depotId]', layerd).blur(function () {
                                depotSelectChange(layerd);
                            });
                            depotsUpdate(depots);
                        },
                        yes: function (index) {
                            var value = makeLogisticsRegionDepotId.val();
                            if (value) {
                                $.ajax('/orderData/logistics/' + orderId, {
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
                }

            }, error: function () {
                layer.msg('服务异常，请稍候重试');
            }
        });
    }).on('click', '.js-settlement', function () {
        // 重新接收端
        $.ajax('/orderData/settlement/' + $(this).attr('data-id'), {
            method: 'put',
            success: function () {
                alert('重新结算完成');
            }
        });
    }).on('click', '.js-mockPay', function () {
        // 模拟支付
        if (!allowMockPay) {
            alert('不支持');
            return;
        }
        $.ajax('/orderData/mockPay/' + $(this).attr('data-id'), {
            method: 'put',
            success: function () {
                table.ajax.reload();
            }
        });
    }).on('click', '.js-quickDone', function () {

        if (!quickUrl) {
            alert('不支持');
            return;
        }
        $.ajax(quickUrl + $(this).attr('data-id'), {
            method: 'put',
            success: function () {
                table.ajax.reload();
            }
        });
    }).on('click', '.js-modifyOrder', function () {
        // $('#content', parent.document).attr('src', 'orderModify.html');
    }).on('click', '.js-quickDoneMore', function () {
        if (!quickUrl) {
            alert('不支持');
            return;
        }
        var self = $(this);
        layer.open({
            content: $('#J_quickDone').html(),
            area: ['500px', 'auto'],
            btn: ['确认', '取消'],
            zIndex: 9999,
            success: function () {
                $('#J_shipmentTime').flatpickr({
                    maxDate: new Date(),
                    locale: 'zh'
                });
                $('#J_deliverTime').flatpickr({
                    minDate: new Date(),
                    locale: 'zh'
                });
            },
            yes: function (index, layero) {
                var value = getValue(layero);
                console.info(value);
                if (value) {
                    $.ajax(quickUrl + self.data('id'), {
                        method: 'post',
                        data: value,
                        success: function () {
                            table.ajax.reload();
                            layer.close(index);
                        }
                    });
                }
            }
        });
    });

    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
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
        // 获取当前tab
        data['status'] = $('.js-orderStatus').find('.active').find('a').attr('data-status');
        return data;
    }

    function clearSearchValue() {
        //TODO
    }

    $('.js-orderMaintain').click(function () {
        $.ajax(
            '/order/orderMaintain', {
                method: 'put',
                success: function () {
                    $._table.ajax.reload();
                }
            }
        );
    });

    function getValue(ele) {
        var data = {};
        var inputs = ele.find('input');
        inputs.each(function () {
            if ($(this).val()) {
                data[$(this).attr('name')] = $(this).val();
            } else {
                layer.tips('请填写该值', $(this), {
                    tipsMore: true
                });
            }
        });
        return inputs.length === Object.keys(data).length ? data : false;
    }
});