$(function () {
    var table = $('#storageForm').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": $('body').data('url'),
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
                "title": "距离", "data": "distance", "name": "distance"
            },
            {
                "title": "操作",
                "className": 'table-action',
                "orderable": false,
                data: function (item) {
                    return '<a href="javascript:;" class="js-operate" data-max="' + item.quantity + '" data-storage="' + item.storage + '"><i class="fa fa-truck"></i>&nbsp;调货</a>';
                }
            }
        ],
        "displayLength": 4
    });

    $(document).on('click', '.js-operate', function () {
        var storage = $(this).data('storage');
        var max = $(this).data('max');
        $('#J_changeStorage').find('input').val(storage);
        $('#J_Quantity').prop({
            'placeholder': storage + '仓现在最大存量为' + max,
            'max': max
        });
        $('#J_storage').removeClass('in');
    });

    $('#J_changeStorage').click(function () {
        $('#J_storage').addClass('in');
    });


    $.validator.addMethod("isPhone", function (value, element) {
        var mobile = /^1([34578])\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "请正确填写的手机号");


    $.validator.setDefaults({
        ignore: '',
        errorElement: "span",
        errorPlacement: function (error, element) {
            error.addClass("help-block");
            if (element.parent('.input-group').length > 0) {
                error.insertAfter(element.parent('.input-group'));
            } else {
                error.insertAfter(element);
            }
        },
        highlight: function (element, errorClass, validClass) {
            if ($(element).parent('.input-group').length > 0) {
                $(element).parent().parent().addClass("has-error").removeClass("has-success");
            } else {
                $(element).parent().addClass("has-error").removeClass("has-success");
            }
        },
        unhighlight: function (element, errorClass, validClass) {
            if ($(element).parent('.input-group').length > 0) {
                $(element).parent().parent().addClass("has-success").removeClass("has-error");
            } else {
                $(element).parent().addClass("has-success").removeClass("has-error");
            }
        },
        submitHandler: function (form) {
            form.submit();
        }
    });

    $('#J_transferForm').validate({
        rules: {
            storage: "required",
            transferQuantity: {
                required: true,
                number: true,
                digits: true
            },
            transferMobile: {
                required: true,
                isPhone: true
            },
            transferContacts: "required"
        },
        messages: {
            storage: "请选择仓储",
            transferQuantity: {
                min: "调货数量不能小于 {0}",
                max: "该仓库产品最大量为 {0}",
                required: "请填写数量",
                digits: "请输入整数"
            },
            transferMobile: {
                required: "请填写联系电话"
            },
            transferContacts: "请填写联系人"
        }
    });

    function forChanges(select, list, name) {
        var target = select.val();
        if (target) {
            var targetValue = list[target];
            if (targetValue) {
                $('[name=' + name + 'Address]').val(targetValue.address);
                $('[name=' + name + 'Name]').val(targetValue.name);
                $('[name=' + name + 'Mobile]').val(targetValue.mobile);
            }
        }
    }

    /*
     其他属性将根据选择内容改变
     可以给予默认选择
     */
    function factorySelectChanged(factory) {
        forChanges(factory, $.factoryCS, 'factory');
    }

    function depotSelectChanged(depot) {
        forChanges(depot, $.depotCS, 'depot');
    }

    var factorySelect = $('#J_deliver_factory');
    var depotSelect = $('#J_deliver_depot');
    depotSelect.change(function () {
        depotSelectChanged(depotSelect);
    });
    factorySelect.change(function () {
        factorySelectChanged(factorySelect);
    });
    depotSelect.blur(function () {
        depotSelectChanged(depotSelect);
    });
    factorySelect.blur(function () {
        factorySelectChanged(factorySelect);
    });
    factorySelectChanged(factorySelect);
    depotSelectChanged(depotSelect);
    $('#J_deliverForm').validate({
        rules: {
            deliverQuantity: {
                required: true,
                number: true,
                digits: true
            },
            deliverMobile: {
                required: true,
                isPhone: true
            },
            receiveMobile: {
                required: true,
                isPhone: true
            },
            deliverContacts: "required",
            receiveContacts: "required"
        },
        messages: {
            deliverQuantity: {
                min: "发货数量不能小于 {0}",
                required: "请填写数量",
                digits: "请输入整数"
            },
            deliverMobile: {
                required: "请填写联系电话"
            },
            receiveMobile: {
                required: "请填写联系电话"
            },
            deliverContacts: "请填写联系人",
            receiveContacts: "请填写联系人"
        }
    });
});