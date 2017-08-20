/**
 * Created by Neo on 2017/7/4.
 */
$(function () {
    $(".i-checks").iCheck({
        checkboxClass: "icheckbox_square-green",
        radioClass: "iradio_square-green"
    });

    $('#J_productForm').validate({
        errorElement: "span",
        errorPlacement: function (error, element) {
            error.addClass("help-block");
            if (element.prop("type") === "checkbox") {
                element.siblings('label').addClass('error');
            } else {
                error.insertAfter(element);
            }
        },
        highlight: function (element, errorClass, validClass) {
            $(element).parent().addClass("has-error").removeClass("has-success");
        },
        unhighlight: function (element, errorClass, validClass) {
            if ($(element).prop("type") === "checkbox") {
                $(element).siblings('label').removeClass('error');
            } else {
                $(element).parent().addClass("has-success").removeClass("has-error");
            }
        },
        submitHandler: function (form) {
            form.submit();
        }
    });


    $('#J_selectCat').chosen();


    $('#J_addCategory').click(function() {
        layer.prompt({
            title: '添加类目',
            formType: 0
        }, function (pass, index) {
            $.ajax('/products/category/', {
                method: 'post',
                contentType: 'text/plain;charset=UTF-8',
                data: pass,
                success: function () {
                    layer.msg('添加成功');
                    $('#J_selectCat').append('<option value="'+pass+'">'+pass+'</option>')
                        .trigger("chosen:updated");
                    layer.close(index);
                },
                error: function () {
                    layer.msg('服务器异常');
                    layer.close(index);
                }
            });
        });
    });
});