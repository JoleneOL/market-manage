$(function () {

    var $body = $('body');
    var $modelSelect = $('#J_modelSelect');
    var $select = $modelSelect.select2({
        theme: "bootstrap",
        width: null,
        containerCssClass: ':all:',
        placeholder: "请输入商品型号",
        allowClear: true,
        language: "zh-CN",
        ajax: {
            url: $body.attr('data-search-url'),
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    search: params.term,
                    page: params.page
                };
            },
            processResults: function (data, params) {
                params.page = params.page || 1;

                return {
                    results: data.items,
                    pagination: {
                        more: (params.page * 30) < data.total_count
                    }
                };
            },
            cache: true
        },
        escapeMarkup: function (markup) {
            return markup;
        },
        minimumInputLength: 2,
        templateResult: formatRepo,
        templateSelection: formatRepoSelection
    }).on("change", function (e) {
        if ($(this).val()) {
            $(this).parent().addClass("has-success").removeClass("has-error");
            $(this).siblings('.error').hide();
        } else {
            $(this).parent().addClass("has-error").removeClass("has-success");
            $(this).siblings('.error').show();
        }
    });
    var selectVal = $modelSelect.data('value');
    var selectId = $modelSelect.data('id');
    if(selectVal && selectId ) $select.append($('<option></option>').val('' + selectId + '').text('' + selectVal + '')).trigger("change");

    function formatRepo(repo) {
        if (repo.loading) return repo.text;
        return '<strong>' + repo.name + '</strong>&nbsp;<em>' + repo.id + '</em>'
    }

    function formatRepoSelection(repo) {
        return repo.name;
    }


    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1([34578])\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");

    $.validator.addMethod("hasCity", function (value, element) {
        var val = $('#J_cityPicker').val();
        return val.split(' ').length === 3;
    }, "请选择完整的地址");

    // 整正数
    $.validator.addMethod("isPositive", function (value, element) {
        var score = /^[0-9]*$/;
        return this.optional(element) || (score.test(value));
    }, "请输入整正数");

    // 最多两位小数
    $.validator.addMethod("isFloat2", function (value, element) {
        var score = /^[0-9]+\.?[0-9]{0,2}$/;
        return this.optional(element) || (score.test(value));
    }, "最多可输入两位小数");


    $('#J_ManualForm').validate({
        ignore: '',
        rules: {
            amount: {
                isPositive: true
            },
            total: {
                isFloat2: true
            },
            fullAddress: {
                required: true
            },
            mobile: {
                isPhone: true
            }
        },
        messages: {
            goodsId: "请填写商品型号",
            storage: "请发货仓库"
        },
        errorElement: "span",
        errorPlacement: function (error, element) {
            error.addClass("help-block");
            element.parent().append(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).parent().addClass("has-error").removeClass("has-success");
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).parent().addClass("has-success").removeClass("has-error");
        },
        submitHandler: function (form) {
            form.submit();
        }
    });

    var readyForDraw = false;


    var table = $('#storageForm').DataTable({
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
        "columns": [
            {
                "title": "仓储仓", "data": "storage", "name": "storage"
            },
            {
                "title": "库存量(台）", "data": "quantity", "name": "quantity"
            },
            {
                "title": "操作",
                "className": 'table-action',
                "orderable": false,
                data: function (item) {
                    return '<a href="javascript:;" class="js-operate" data-storage="' + item.storage + '"><i class="fa fa-check-square-o"></i>&nbsp;选择</a>';
                }
            }
        ],
        "displayLength": 4,
        "retrieve": true,
        "preDrawCallback": function () {
            return readyForDraw;
        }
    });

    $(document).on('click', '.js-operate', function () {
        var storage = $(this).data('storage');
        $('#J_changeStorage')
            .find('input').val(storage)
            .end()
            .next().val(storage);
        $('#J_storage').removeClass('in');
    });


    function extendData() {
        var data = {};
        data.product = $('select[name="goodsId"]').val();
        data.amount = $('input[name="amount"]').val();
        return data;
    }


    $('#J_changeStorage').click(function () {
        var goodsId = $('select[name="goodsId"]').val();
        var goodsAmount = $('input[name="amount"]').val();

        if (goodsId && goodsAmount) {
            $('#J_storage').addClass('in');
            readyForDraw = true;
            table.ajax.reload();
        } else {
            layer.msg('型号或者数量不能为空');
        }

    });
});