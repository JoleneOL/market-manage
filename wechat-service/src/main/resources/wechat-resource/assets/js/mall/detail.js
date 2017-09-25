$(function () {
    var swiper = new Swiper('.swiper-container', {
        pagination: '.swiper-pagination',
        autoplay: 3000,
        slidesPerView: 1,
        paginationClickable: true,
        observer: true,
        observeParents: true,
        updateOnImagesReady: true,
        loop: true
    });
    var animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend';

    $('.js-open-menu').click(function () {
        $('.flick-menu-mask').show();
        $('.spec-menu').show().addClass('spec-menu-show').one(animationEnd, function () {
            $(this).removeClass('spec-menu-show');
        });
        ;
    });

    $('.js-closed-menu').click(function () {
        $('.spec-menu').addClass('spec-menu-hide').one(animationEnd, function () {
            $(this).hide().removeClass('spec-menu-hide');
            $('.flick-menu-mask').hide();
        });
    });
});