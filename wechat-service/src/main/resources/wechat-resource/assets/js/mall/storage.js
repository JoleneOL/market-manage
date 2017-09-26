var Storage = {
    setData: function (data) {
        var self = this;
        var storage = self.getData();
        $.extend(storage, data);
        localStorage.setItem('_cart', JSON.stringify(storage));
    },
    getData: function () {
        var storage = JSON.parse(localStorage.getItem('_cart'));
        storage = storage ? storage : {};
        return storage;
    },
    clear: function () {

    }
};

$('.js-cartBtn').click(function () {
    $('#J_cartForm')
        .find('input').val(JSON.stringify(Storage.getData()))
        .end()
        .submit();
});