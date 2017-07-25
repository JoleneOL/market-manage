$(function () {

    var myChart = echarts.init(document.getElementById('J_storage'));

    var option = {
        title : {
            text: '总体仓库库存',
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: ['厨下净水机','立式净水器','量子厨下净水机','量子立式净水机','量子空气净化器','量子食品优化宝','量子防辐射芯片','量子水宝']
        },
        series : [
            {
                name: '访问来源',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data:[
                    {value:300, name:'厨下净水机'},
                    {value:310, name:'立式净水器'},
                    {value:234, name:'量子厨下净水机'},
                    {value:335, name:'量子立式净水机'},
                    {value:535, name:'量子空气净化器'},
                    {value:135, name:'量子食品优化宝'},
                    {value:335, name:'量子防辐射芯片'},
                    {value:1548, name:'量子水宝'}
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

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);


    var table = $('#storageForm').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $('body').data('url'),
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
                "title": "物流公司", "data": "logistics", "name": "logistics"
            },
            {
                "title": "仓储仓", "data": "storage", "name": "storage"
            },
            {
                "title": "商品名称", "data": "goods", "name": "goods"
            },
            {
                "title": "库存量(台）", "data": "inventory", "name": "inventory"
            },
            {
                "title": "最新入库时间", "data": "storageTime", "name": "storageTime"
            },
            {
                "title": "操作员", "data": "operator", "name": "operator"
            },
            {
                "title": "操作",
                "className": 'table-action',
                "orderable": false,
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-operate" data-id="' + item.id + '"><i class="fa fa-truck"></i>&nbsp;发货</a>';
                    var b = '<a href="javascript:;" class="js-info" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;查看</a>';
                    return a + b;
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

        table.ajax.reload();
    }).on('click', '.js-operate', function () {
        window.location.href = '_repairDetail.html'
    }).on('click', '.js-info', function () {
        window.location.href = '_repairOperate.html'
    });

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