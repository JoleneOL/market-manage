$(function () {

    var $body = $('body');

    $('#waitMsg').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $body.data('wait-url')
        },
        "ordering": false,
        "lengthChange": false,
        "searching": false,
        "colReorder": false,
        "columns": [
            {
                "data": "message"
            },
            {
                "data": "time"
            },
            {
                "className": 'table-action',
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-operate" data-id="' + item.id + '">发货</a>';
                    var b = '<a href="javascript:;" class="js-transfer" data-id="' + item.id + '">调仓</a>';
                    var c = '<a href="javascript:;" class="view-link-disabled" data-id="' + item.id + '">已处理</a>';
                    if (item.stateCode === 0) return a + b;
                    return c;
                }
            }
        ],
        "displayLength": 15,
        "drawCallback": function ( oSettings ) {
            $(oSettings.nTHead).hide();
        }
    });

    $('#warnMsg').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $body.data('warn-url')
        },
        "ordering": false,
        "lengthChange": false,
        "searching": false,
        "colReorder": false,
        "columns": [
            {
                "data": "message"
            },
            {
                "data": "time"
            },
            {
                "className": 'table-action',
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-operate" data-id="' + item.id + '">发货</a>';
                    var c = '<a href="javascript:;" class="view-link-disabled" data-id="' + item.id + '">已处理</a>';
                    if (item.stateCode === 0) return a;
                    return c;
                }
            }
        ],
        "displayLength": 15,
        "drawCallback": function ( oSettings ) {
            $(oSettings.nTHead).hide();
        }
    });


    $(document).on('click', '.js-operate', function () {
        window.location.href = '_delivery.html'
    }).on('click', '.js-transfer', function () {
        window.location.href = '_transfer.html'
    });
});