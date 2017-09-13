$(function () {
    $(document).on('mouseenter', '.text', function () {
        var detail = $(this).find('.hide');
        if (detail.length > 0) {
            layer.msg(detail.html(), {
                offset: '20%'
            });
        }

    }).on('mouseleave', '.text', function () {
        layer.closeAll();
    });

    var time = '';
    $('.date').each(function () {
        $(this).text() !== time ? time = $(this).text() : $(this).addClass('invisible').siblings('.week').addClass('invisible');
    });

    var confirmAble = $('#warningMessage').size() > 0;
    var id = $('body').data('id');

    // 强行模拟物流事件
    function doReject(index) {
        $.ajax('/api/logisticsEventReject/' + id, {
            method: 'put',
            success: function () {
                if (index)
                    layer.close(index);
                location.reload();
            }
        });
    }

    function doSuccess(index) {
        $.ajax('/api/logisticsEventSuccess/' + id, {
            method: 'put',
            success: function () {
                if (index)
                    layer.close(index);
                location.reload();
            }
        });
    }

    function readLocalStorage(key) {
        if (!localStorage)
            return '';
        return localStorage.getItem(key) || '';
    }

    function updateLocalStorage(key, value) {
        if (!localStorage)
            return '';
        localStorage.setItem(key, value);
    }

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

    function doInstall(index) {
        if (index)
            layer.close(index);

        layer.open({
            title: '填写安装信息',
            content: $('#InstallEventRegion').html(),
            area: ['510px', '300px'],
            zIndex: 9999,
            success: function (ui) {
                $('input[name=installer]', ui).val(readLocalStorage('LastInstallInstaller'));
                $('input[name=installCompany]', ui).val(readLocalStorage('LastInstallInstallCompany'));
                $('input[name=mobile]', ui).val(readLocalStorage('LastInstallMobile'));
            }, yes: function (index2, ui) {
                var value = getValue(ui);
                if (value) {
                    // 保存
                    updateLocalStorage('LastInstallInstaller', value.installer);
                    updateLocalStorage('LastInstallInstallCompany', value.installCompany);
                    updateLocalStorage('LastInstallMobile', value.mobile);
                    $.ajax('/api/logisticsEventInstall/' + id, {
                        method: 'put',
                        data: value,
                        success: function () {
                            layer.close(index2);
                            location.reload();
                        }
                    });
                }
            }
        });

    }

    $('#mockReject').click(function () {
        if (confirmAble)
            layer.confirm('请确保物流订单「真实」地处于其他状态，而本系统未曾受理！', {yes: doReject});
        else
            doReject();
    });

    $('#mockSuccess').click(function () {
        if (confirmAble)
            layer.confirm('请确保物流订单「真实」地处于其他状态，而本系统未曾受理！', {yes: doSuccess});
        else
            doSuccess();
    });

    $('#mockInstall').click(function () {
        if (confirmAble)
            layer.confirm('请确保物流订单「真实」地处于其他状态，而本系统未曾受理！', {yes: doInstall});
        else
            doInstall();
    });

});