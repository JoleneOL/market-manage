/**
 * Created by Neo on 2017/5/10.
 */
$(function () {

    var $comm = $("#commission");
    // display:none 影响子元素高度的获取
    $comm.addClass('goAway');

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

    var extraHeight = 0;
    $('.js-extra-h-c').each(function () {
        extraHeight += $(this).outerHeight(true);
    });
    $('.swiper-slide').height($(window).height() - Math.ceil(extraHeight) - 52);


    var maintainURL = 'maintain.html';
    var maintainStatusURL = 'maintainStatus.html';

    var repairURL = 'repair.html';
    var repairStatusURL = 'repairStatus.html';

    var commTpl = function (obj) {
        return '<div class="view-comm-list_item"> ' +
            '<div class="weui-flex"> ' +
            '<div class="weui-flex__item">' + obj.commType + '</div> ' +
            '<div class="weui-flex__item">' + obj.name + '</div> ' +
            '<div class="weui-flex__item"><strong>￥' + obj.commission + '</strong></div> ' +
            '</div> ' +
            '<div class="weui-flex"> ' +
            '<div class="weui-flex__item">(' + obj.divided + '分成）</div> ' +
            '<div class="weui-flex__item">' + obj.commInfo + '</div> ' +
            '<div class="weui-flex__item text-gray">' + obj.commTime + '</div> ' +
            '</div> ' +
            '</div>';
    };

    $('.js-commItems').each(function () {
        var self = $(this);
        self.myScroll({
            ajaxUrl: self.attr('data-url'),
            template: commTpl
        });
    });

    $('#equipment').myScroll({
        ajaxUrl: '/api/equipmentList',
        template: function (obj) {
            return '<div class="view-preview view-mb-20">' +
                '<div class="view-preview_hd"> ' +
                '<p class="view-preview_header_value pull-left">饮水机编号：' + obj.id + '</p> ' +
                equipmentTpl.status(obj) +
                '<p class="view-preview_header_value">产品型号：' + obj.model + '</p>' +
                '</div> ' +
                '<div class="view-preview_bd view-bg-color-f2"> ' +
                equipmentTpl.content(obj) +
                '</div> ' +
                '<div class="view-preview_sub"> ' +
                equipmentTpl.sub(obj) +
                '</div> ' +
                equipmentTpl.buttons(obj) +
                '</div>';
        }
    });
    var equipmentTpl = {
        status: function (obj) {
            var dom = '';
            switch (obj.status) {
                case 0:
                    dom = '<span class="view-preview_header_label pull-right text-success">正常使用中</span>';
                    break;
                case 1:
                    dom = '<span class="view-preview_header_label pull-right text-warn">维护中</span>';
                    break;
                case 2:
                    dom = '<span class="view-preview_header_label pull-right text-primary">已移机</span>';
                    break;
            }
            return dom;
        },
        content: function (obj) {
            var dom = '';
            switch (obj.status) {
                case 0:
                    dom = '<div class="view-preview_body_item">' +
                        '<span class="weui-form-preview__label">使用费</span>' +
                        '<p class="weui-form-preview__value">￥' + obj.cost + '</p>' +
                        '</div>' +
                        '<div class="view-preview_body_item">' +
                        '<span class="weui-form-preview__label">使用年限</span>' +
                        '<p class="weui-form-preview__value">' + obj.years + '</p>' +
                        '</div>';
                    break;
                case 1:
                    dom = '<div class="view-preview_body_item">' +
                        '<span class="weui-form-preview__label">使用费</span>' +
                        '<p class="weui-form-preview__value">￥' + obj.cost + '</p>' +
                        '</div>' +
                        '<div class="view-preview_body_item">' +
                        '<span class="weui-form-preview__label">使用年限</span>' +
                        '<p class="weui-form-preview__value">' + obj.years + '</p>' +
                        '</div>' +
                        '<div class="view-preview_body_item">' +
                        '<span class="weui-form-preview__label">服务内容</span>' +
                        '<p class="weui-form-preview__value">' + obj.service + '</p>' +
                        '</div>';
                    break;
                case 2:
                    dom = '<div class="view-preview_body_item">' +
                        '<span class="weui-form-preview__label">移机地址</span>' +
                        '<p class="weui-form-preview__value">' + obj.transferAddress + '</p>' +
                        '</div>' +
                        '<div class="view-preview_body_item">' +
                        '<span class="weui-form-preview__label">联系人</span>' +
                        '<p class="weui-form-preview__value">' + obj.transferPhone + '</p>' +
                        '</div>' +
                        '<div class="view-preview_body_item">' +
                        '<span class="weui-form-preview__label">联系电话</span>' +
                        '<p class="weui-form-preview__value">' + obj.transferUser + '</p>' +
                        '</div>';
                    break;
            }
            return dom;
        },
        sub: function (obj) {
            if (obj.status === 2) {
                return '<div class="view-preview_body_item"><span>申请移机时间：' + obj.transferTime + '</span></div>'
            }
            return '<div class="view-preview_body_item"><span>TDS值：<strong>' + obj.TDS + '</strong></span></div>' +
                '<div class="view-preview_body_item"><span>安装地址：' + obj.installationAddress + '</span></div>' +
                '<div class="view-preview_body_item"><span>安装时间：' + obj.installationTime + '</span></div> ';
        },
        buttons: function (obj) {
            var dom = '';
            if (obj.status !== 2) {
                dom = '<div class="view-preview_ft">' +
                    '<div class="button_sp_area">' +
                    '<a href="' + maintainURL + '?equipmentId=' + obj.equipmentId + '" class="view-btn">维护</a>' +
                    '</div>' +
                    '</div>';
            }
            return dom;
        }
    };


    $comm.removeClass('goAway');
});