/**
 * Created by Neo on 2017/5/8.
 */
$(function () {

    if(typeof moment === 'function') {
        var date = moment().format('YYYY-MM-DD HH:mm');
        $("#J_maintainTime").datetimePicker({
            title: '预约时间',
            min: date
        });
    }

    $('#J_repairType, #J_Reason, #J_Logistics').change(function () {
        var $extra = $(this).closest('.weui-cell_select').next('.J_extra');
        if ($(this).val() === 'other') {
            $extra.show();
        } else {
            $extra.hide();
        }
    });

    $('#J_Cancel').click(function () {
        $.confirm("您确定取消售后服务么？", "确认取消？", function() {
            $('#J_form').submit();
        }, function() {
            //取消操作
        });
    });
});