$(function () {

    var myChart = echarts.init(document.getElementById('J_storage'));

    var option = {
        title: {
            text: '总体仓库库存',
            x: 'center'
        },
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: $.allProduct
        },
        series: [
            {
                name: '可用库存',
                type: 'pie',
                radius: '55%',
                center: ['50%', '60%'],
                data: $.allProductAmount,
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
        "paging": true,
        "ordering": true,
        "lengthChange": false,
        "searching": false,
        "colReorder": true,
        "columns": [
            {
                "title": "仓库类型", "data": "storageType", "name": "storageType"
            },
            {
                "title": "仓库", "data": "storage", "name": "storage"
            },
            {
                "title": "货品", "data": "product", "name": "product"
            },
            {
                "title": "库存量", "data": "inventory", "name": "inventory"
            },
            {
                "title": "操作",
                "className": 'table-action',
                "orderable": false,
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-operate" data-depotId="' + item.depotId
                        + '" data-productCode="' + item.productCode + '"><i class="fa fa-truck"></i>&nbsp;发货</a>';
                    // var b = '<a href="javascript:;" class="js-info" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;查看</a>';
                    // return a + b;
                    return a;
                }
            }
        ],
        // "displayLength": 15,
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

    $('.js-addAsRoot').click(function () {
        var self = $(this);
        layer.open({
            content: '警告！请谨慎操作！这个库存更新将不会被任何审核管制！',
            // area: ['500px', 'auto'],
            btn: ['确认', '取消'],
            zIndex: 9999,
            yes: function (index) {
                self.closest('form').submit();
            }
        });
    });

    $(document).on('click', '.js-search', function () {
        table.ajax.reload();
    }).on('click', '.js-operate', function () {
        var _this = $(this);
        window.location.href = $('#J_DeliveryLink').attr('href') + '?depotId=' + _this.attr('data-depotId')
            + "&productCode=" + encodeURIComponent(_this.attr('data-productCode'));
    }).on('click', '.js-info', function () {
        window.location.href = '_storageDetail.html'
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