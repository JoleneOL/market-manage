/**
 * Created by Chang on 2017/5/1.
 */
$(function () {
    "use strict";

    var $invoice = $('#J_haveInvoice');
    $('.js-invoice').change(function () {
       if($(this).val() === '1') {
           $invoice.slideDown(200);
       } else {
           $invoice.slideUp(200);
       }
    })
});
