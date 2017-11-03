$(function () {
    var $searchInput = $('#J_searchInput');
    var $searchMask = $('#J_searchMask');
    $searchInput.focus(function () {
        $searchMask.show();
        $('body').css('overflow', 'hidden');
    });

    $('#searchCancel').click(function () {
        $searchMask.hide().empty();
        $('body').css('overflow', 'inherit');
    });

    $searchInput.on('keypress', function (e) {
        var keyCode = e.keyCode;
        var input = $(this).val();
        if (keyCode === 13) {
            e.preventDefault();
            if ($.trim(input)) {
                $.showLoading();
                ajaxSearch(input);
            }
        }
    });
    var _body = $('body');
    function ajaxSearch(query) {
        $.ajax(_body.attr('data-search-url'), {
            method: 'GET',
            dataType: 'json',
            data: {
                title: query
            },
            success: function (res) {
                $.hideLoading();
                if (res.resultCode !== 200) {
                    $.toast('请求失败', 'cancel');
                    return '';
                }
                console.log(res);
                renderList(res.data);
            },
            error: function () {
                $.hideLoading();
                $.toast('服务器异常', 'cancel');
            }
        });
    }

    function renderList(data) {
        var str = '';
        if (data.length > 0) {
            str = '<div class="weui-cells__title">相关问题</div><div class="weui-cells">';
            $.each(data, function (i, v) {
                str += '<a class="weui-cell weui-cell_access" href="helpDetail.html?id=' + v.helpId + '" th:href="@{/commonProblemDetail/}'+v.id+'" >' +
                    '  <div class="weui-cell__bd" th:text="'+v.title+'">' + v.helpTitle + '</div>' +
                    '  <div class="weui-cell__ft"></div>' +
                    '</a>'
            });
            str += '</div>'
        } else {
            str = '<p class="no-result">未搜索到相关的问题<br>请联系客服咨询：<a href="tel:0571-88187913" class="text-black">0571-88187913</a></p>'
        }
        $searchMask.html(str);
    }
});