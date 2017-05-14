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
    var maintainStatusURL = 'maintainStatus.html';

    var repairURL = 'repair.html';
    var repairStatusURL = 'repairStatus.html';

    $('#equipment').myScroll({
        ajaxUrl: '/api/equipmentList',
        template: function (obj) {
            return '<div class="view-preview view-mb-20">' +
                '<div class="view-preview_hd"> ' +
                '<p class="view-preview_header_value pull-left">饮水机编号：' + obj.id + '</p> ' +
                equipmentTpl.status(obj.equipmentStatus) +
                '</div> ' +
                '<div class="view-preview_bd view-bg-color-f2"> ' +
                '<div class="view-preview_body_item"> ' +
                '<span>剩余使用时间：</span><p>' + obj.remainingTime + '</p> ' +
                '</div> ' +
                '<div class="view-preview_body_item">' +
                '<span>TDS值：</span><p>' + obj.TDS + '</p>' +
                '</div>' +
                '</div> ' +
                '<div class="view-preview_sub"> ' +
                '<div class="view-preview_body_item"><span>安装地址：' + obj.installationAddress + '</span></div>' +
                '<div class="view-preview_body_item"><span>安装时间：' + obj.installationTime + '</span></div> ' +
                '</div> ' +
                '<div class="view-preview_ft"> ' +
                '<div class="button_sp_area"> ' +
                equipmentTpl.buttons(obj.equipmentId, obj.equipmentStatus) +
                '</div> ' +
                '</div> ' +
                '</div>';
        }
    });
    var equipmentTpl = {
        status: function (status) {
            var dom = '';
            switch (status) {
                case 0:
                    dom = '<span class="view-preview_header_label pull-right text-success">正常使用中</span>';
                    break;
                case 1:
                    dom = '<span class="view-preview_header_label pull-right text-primary">维护中</span>';
                    break;
                case 2:
                    dom = '<span class="view-preview_header_label pull-right text-warn">维修中</span>';
                    break;
            }
            return dom;
        },
        buttons: function (id, status) {
            var dom = '';
            switch (status) {
                case 0:
                    dom = '<a href="' + maintainURL + '?equipmentId=' + id + '" class="view-btn">维护</a> ' +
                        '<a href="' + repairURL + '?equipmentId=' + id + '" class="view-btn">维修</a> ';
                    break;
                case 1:
                    dom = '<a href="' + maintainStatusURL + '?equipmentId=' + id + '" class="view-btn">维护中</a> ' +
                        '<a href="javascript:;" class="view-btn view-btn_disabled">维修</a> ';
                    break;
                case 2:
                    dom = '<a href="javascript:;" class="view-btn view-btn_disabled">维护</a> ' +
                        '<a href="' + repairStatusURL + '?equipmentId=' + id + '" class="view-btn">维修中</a> ';
                    break;
            }
            return dom;
        }
    };
});