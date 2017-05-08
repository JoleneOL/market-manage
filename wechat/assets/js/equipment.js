/**
 * Created by Neo on 2017/5/8.
 */
$(function () {
    var date = moment().format('YYYY-MM-DD HH:mm');
    var $extra = $('.J_extra');
    $("#J_maintainTime").datetimePicker({
        title: '预约时间',
        min: date
    });

    $('#J_repairType').change(function () {
        if ($(this).val() === 'other') {
            $extra.show();
        } else {
            $extra.hide();
        }
    });
});