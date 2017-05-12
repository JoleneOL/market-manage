/**
 * Created by Neo on 2017/5/10.
 */
$(function () {
    var tabsItem = $('.view-tabs_item');
    var tabsSwiper = $('#tabs-container').swiper({
        observer: true,
        observeParents: true,
        speed: 500,
        onSlideChangeStart: function () {
            $(".view-tabs .active").removeClass('active');
            tabsItem.eq(tabsSwiper.activeIndex).addClass('active');
        }
    });
    tabsItem.on('touchstart mousedown', function (e) {
        e.preventDefault();
        $(".view-tabs .active").removeClass('active');
        $(this).addClass('active');
        tabsSwiper.slideTo($(this).index())
    });
    tabsItem.click(function (e) {
        e.preventDefault();
    });
    var maintainURL = 'maintain.html';
    var repairURL = 'repair.html';
    var refundURL = 'refund.html';

    var maintainStatusURL = 'maintainStatus.html';
    var repairStatusURL = 'repairStatus.html';
    var refundStatusURL = 'refundStatus.html';

    $('#equipment').myScroll({
        ajaxUrl: '/api/equipmentList',
        template: function (obj) {
            return '<div class="weui-form-preview view-mb-20"> ' +
                '<div class="view-form-preview-ex"> ' +
                '<div class="weui-form-preview__item"> ' +
                '<p class="weui-form-preview__value">饮水机编号：' + obj.id + '</p> ' +
                equipmentTpl.status(obj.equipmentStatus) +
                '</div> ' +
                '</div> ' +
                '<div class="weui-form-preview__hd view-form-text_left view-bg-color-f2"> ' +
                '<div class="weui-form-preview__item"> ' +
                '<label class="weui-form-preview__label">剩余使用时间：</label> ' +
                '<em class="weui-form-preview__value">' + obj.remainingTime + '</em> ' +
                '</div> ' +
                '<div class="weui-form-preview__item"> ' +
                '<label class="weui-form-preview__label">TDS值：</label> ' +
                '<em class="weui-form-preview__value">' + obj.TDS + '</em> ' +
                '</div> ' +
                '</div> ' +
                '<div class="weui-form-preview__bd"> ' +
                '<div class="weui-form-preview__item"> ' +
                '<label class="weui-form-preview__label"></label> ' +
                '<span class="weui-form-preview__value">安装地址：' + obj.installationAddress + '</span> ' +
                '</div> ' +
                '<div class="weui-form-preview__item"> ' +
                '<span class="weui-form-preview__value">安装时间：' + obj.installationTime + '</span> ' +
                '</div> ' +
                '</div> ' +
                '<div class="weui-form-preview__ft view_form-button-group"> ' +
                '<div class="button_sp_area"> ' +
                equipmentTpl.buttons(obj.equipmentId, obj.equipmentStatus) +
                '</div> ' +
                '</div>' +
                '</div>';
        }
    });

    var equipmentTpl = {
        status: function (status) {
            var dom = '';
            switch (status) {
                case 0:
                    dom = '<label class="weui-form-preview__label text-success">正常使用中</label>';
                    break;
                case 1:
                    dom = '<label class="weui-form-preview__label text-primary">维护中</label>';
                    break;
                case 2:
                    dom = '<label class="weui-form-preview__label text-warn">维修中</label>';
                    break;
                case 3:
                    dom = '<label class="weui-form-preview__label text-error">退款中</label>';
                    break;
            }
            return dom;
        },
        buttons: function (id, status) {
            var dom = '';
            switch (status) {
                case 0:
                    dom = '<a href="' + maintainURL + '?equipmentId=' + id + '" class="weui-btn weui-btn_mini weui-btn_default">维护</a> ' +
                        '<a href="' + repairURL + '?equipmentId=' + id + '" class="weui-btn weui-btn_mini weui-btn_default">维修</a> ' +
                        '<a href="' + refundURL + '?orderId=' + id + '" class="weui-btn weui-btn_mini weui-btn_default">退款</a> ';
                    break;
                case 1:
                    dom = '<a href="' + maintainStatusURL + '?equipmentId=' + id + '" class="weui-btn weui-btn_mini weui-btn_default">维护中</a> ' +
                        '<a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_default weui-btn_disabled">维修</a> ' +
                        '<a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_default weui-btn_disabled">退款</a> ';
                    break;
                case 2:
                    dom = '<a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_default weui-btn_disabled">维护</a> ' +
                        '<a href="' + repairStatusURL + '?equipmentId=' + id + '" class="weui-btn weui-btn_mini weui-btn_default">维修中</a> ' +
                        '<a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_default weui-btn_disabled">退款中</a> ';
                    break;
                case 3:
                    dom = '<a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_default weui-btn_disabled">维护</a> ' +
                        '<a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_default weui-btn_disabled">维修</a> ' +
                        '<a href="' + refundStatusURL + '?equipmentId=' + id + '" class="weui-btn weui-btn_mini weui-btn_default">退款中</a> ';
                    break;
            }
            return dom;
        }
    };
});