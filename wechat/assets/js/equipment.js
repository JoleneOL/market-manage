/**
 * Created by Neo on 2017/5/8.
 */
$(function () {
    var date = moment().format('YYYY-MM-DD HH:mm');

    $("#J_maintainTime").datetimePicker({
        title: '预约时间',
        min: date
    });
});