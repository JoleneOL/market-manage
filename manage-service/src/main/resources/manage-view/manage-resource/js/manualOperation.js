$(function () {

    $('#J_modelSelect').select2({
        theme: "bootstrap",
        width: null,
        containerCssClass: ':all:',
        placeholder: "请输入商品型号",
        allowClear: true,
        language: "zh-CN",
        ajax: {
            url: $('body').attr('data-search-url'),
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
        if ( $(this).val()) {
            $(this).parent().addClass("has-success").removeClass("has-error");
            $(this).siblings('.error').hide();
        } else {
            $(this).parent().addClass("has-error").removeClass("has-success");
            $(this).siblings('.error').show();
        }
    });

    function formatRepo(repo) {
        if (repo.loading) return repo.text;
        return '<strong>' + repo.goods + '</strong>&nbsp;<em>' + repo.model + '</em>'
    }

    function formatRepoSelection(repo) {
        return repo.model || repo.text;
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
        ignore: "",
        rules: {
            fullAddress: {
                required: true,
                hasCity: true
            }
        },
        messages: {
            goodsModel: "请填写商品型号",
            fullAddress: {
                required: '请填写详细地址'
            },
            haierCode: "请填写仓库编号"
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

});