/**
 * Created by Chang on 2017/5/7.
 */
$(function () {
    $('#J_date').datetimepicker({
        todayBtn: "linked",
        language: "zh-CN",
        autoclose: true,
        format: "yyyy-m-d",
        clearBtn: true,
        todayHighlight: true,
        minView: 2,
        startDate: new Date()
    });

    $('#J_time').datetimepicker({
        language: "zh-CN",
        format: "h:i",
        autoclose: true,
        clearBtn: true,
        startView: 1,
        minView: 0,
        maxView: 1
    });
});