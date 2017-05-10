/**
 * Created by Neo on 2017/5/10.
 */
$(function () {

    var amount = $('#J_withdrawAmount');
    var allInInput = $('#J_allInVal');
    $('#J_Bank').on('keyup mouseout input', function () {
        var $this = $(this);
        var v = $this.val();
        /\S{5}/.test(v) && $this.val(v.replace(/\s/g, '').replace(/(.{4})/g, "$1 "));
    });
    allInInput.on('keyup mouseout input', function () {
        var $this = $(this);
        var v = $this.val();
        if(v) {
            $this.addClass('view-input-big');
        } else  {
            $this.removeClass('view-input-big');
        }
        amount.text(v);
    });

    $('#J_allInBtn').click(function () {
        allInInput
            .val($(this).attr('data-all-in'))
            .addClass('view-input-big');

        amount.text(allInInput.val());
    });

    var $invoice = $('#J_extra');
    $('.js-invoice').change(function () {
        if($(this).val() === '1') {
            $invoice.removeClass('displayNone');
        } else {
            $invoice.addClass('displayNone');
        }
    })
});