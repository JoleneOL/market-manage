$(function () {
    var detailUrl = $('body').attr('data-detail-url');
    // var receive = echarts.init(document.getElementById('J_waitReceive'));
    // var install = echarts.init(document.getElementById('J_waitInstall'));

    var optionReceive = {
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            right: true,
            data: ['厨下净水机', '立式净水器', '量子厨下净水机', '量子立式净水机', '量子空气净化器', '量子食品优化宝', '量子防辐射芯片', '量子水宝']
        },
        series: [
            {
                name: '待收货总量',
                type: 'pie',
                radius: '65%',
                center: ['35%', '40%'],
                data: [
                    {value: 40, name: '厨下净水机'},
                    {value: 12, name: '立式净水器'},
                    {value: 34, name: '量子厨下净水机'},
                    {value: 35, name: '量子立式净水机'},
                    {value: 12, name: '量子空气净化器'},
                    {value: 13, name: '量子食品优化宝'},
                    {value: 33, name: '量子防辐射芯片'},
                    {value: 48, name: '量子水宝'}
                ],
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };

    var optionInstall = {
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            right: true,
            data: ['厨下净水机', '立式净水器', '量子厨下净水机', '量子立式净水机', '量子空气净化器', '量子食品优化宝', '量子防辐射芯片', '量子水宝']
        },
        series: [
            {
                name: '待安装总量',
                type: 'pie',
                radius: '65%',
                center: ['35%', '40%'],
                data: [
                    {value: 30, name: '厨下净水机'},
                    {value: 10, name: '立式净水器'},
                    {value: 23, name: '量子厨下净水机'},
                    {value: 35, name: '量子立式净水机'},
                    {value: 53, name: '量子空气净化器'},
                    {value: 13, name: '量子食品优化宝'},
                    {value: 45, name: '量子防辐射芯片'},
                    {value: 36, name: '量子水宝'}
                ],
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };

    // receive.setOption(optionReceive);
    // install.setOption(optionInstall);


    $('#J_datePicker').flatpickr({
        maxDate: new Date(),
        locale: 'zh'
    });

    var $body = $('body');

    var logistics = $('#logisticsForm').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $body.data('logistics-url'),
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
                "title": "订单号", "data": "orderId", "name": "orderId"
            },
            {
                "title": "物流编号", "data": "supplierId", "name": "supplierId"
            },
            {
                "title": "订单时间", "data": "orderTime", "name": "orderTime"
            },
            {
                "title": "收货地址", "data": "address", "name": "address"
            },
            {
                "title": "购买用户", "data": "orderUser", "name": "orderUser"
            },
            {
                "title": "手机号", "data": "mobile", "name": "mobile"
            },
            // {
            //     "title": "物流公司", "data": "logistics", "name": "logistics"
            // },
            {
                "title": "物流仓储", "data": "storage", "name": "storage"
            },
            // {
            //     "title": "安装公司", "data": "installation", "name": "installation"
            // },
            {
                "title": "发货时间", "data": "deliverTime", "name": "deliverTime"
            },
            {
                "title": "物流状态", "data": "status", "name": "status"
            },
            {
                "title": "操作",
                "className": 'table-action',
                "orderable": false,
                data: function (item) {
                    return '<a href="javascript:;" class="js-info" data-unit-id="' + item.unitId + '"><i class="fa fa-check-circle-o"></i>&nbsp;查看物流</a>';
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

    var factory = $('#factoryForm').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $body.data('factory-url'),
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
                "title": "物流编号", "data": "supplierId", "name": "supplierId"
            },
            // {
            //     "title": "总发货量", "data": "deliverQuantity", "name": "deliverQuantity"
            // },
            {
                "title": "订单时间", "data": "orderTime", "name": "orderTime"
            },
            // {
            //     "title": "发货工厂", "data": "deliverFactory", "name": "deliverFactory"
            // },

            {
                "title": "收货仓库", "data": "depotName", "name": "depotName"
            },
            {
                "title": "收货地址", "data": "address", "name": "address"
            },
            {
                "title": "联系人", "data": "contacts", "name": "contacts"
            },
            {
                "title": "联系电话", "data": "mobile", "name": "mobile"
            },
            {
                "title": "状态", "data": "status", "name": "status"
            },
            {
                "title": "操作",
                "className": 'table-action',
                "orderable": false,
                data: function (item) {
                    return '<a href="javascript:;" class="js-info" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;查看</a>';
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

    var storage = $('#storageForm').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $body.data('storage-url'),
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
                "title": "订单号", "data": "orderId", "name": "orderId"
            },
            {
                "title": "订单时间", "data": "orderTime", "name": "orderTime"
            },
            {
                "title": "调货机型", "data": "goods", "name": "goods"
            },
            {
                "title": "调货数量", "data": "transferQuantity", "name": "transferQuantity"
            },
            {
                "title": "调货仓储", "data": "transferStorage", "name": "transferStorage"
            },
            {
                "title": "发货时间", "data": "deliverTime", "name": "deliverTime"
            },
            {
                "title": "收货仓储", "data": "deliverStorage", "name": "deliverStorage"
            },
            {
                "title": "联系人", "data": "contacts", "name": "contacts"
            },
            {
                "title": "联系电话", "data": "mobile", "name": "mobile"
            },
            {
                "title": "状态", "data": "status", "name": "status"
            },
            {
                "title": "操作",
                "className": 'table-action',
                "orderable": false,
                data: function (item) {
                    return '<a href="javascript:;" class="js-info" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;查看</a>';
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
        var table = $('.tab-pane.active').attr('id');
        if (table === 'logistics') return logistics.ajax.reload();
        if (table === 'factory') return factory.ajax.reload();
        if (table === 'storage') return storage.ajax.reload();
    }).on('click', '.js-info', function () {
        var from = $(this).closest('.tab-pane').attr('id');
        var table = $('.tab-pane.active').attr('id');
        if (table === 'factory')
            window.location.href = detailUrl + '?id=' + $(this).data('id');
        else if (table === 'logistics') {
            var unitId = $(this).data('unit-id');
            if (unitId) {
                window.location.href = detailUrl + '?id=' + unitId;
            }//否则是查看订单详情
        }
    });

    $('input[name="status"]').on('change', function () {
        var table = $('.tab-pane.active').attr('id');
        if (table === 'logistics') return logistics.ajax.reload();
        if (table === 'factory') return factory.ajax.reload();
        if (table === 'storage') return storage.ajax.reload();
    });

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

        var table = $('.tab-pane.active');
        var status = $('input[name="status"]:checked', table).val();
        data['status'] = status ? status : 'all';
        return data;
    }

    function clearSearchValue() {
        //TODO
    }

    var tabHash = window.location.hash;
    $(tabHash).addClass('active').siblings().removeClass('active');
    $('a[href="' + tabHash + '"]').parent().addClass('active').siblings().removeClass('active');
});