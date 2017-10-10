var Storage = {
    setData: function (data) {
        console.log("data:" + data);
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
    clear: function (key) {
        var storage = this.getData();
        delete storage[key];
        localStorage.setItem('_cart', JSON.stringify(storage));
    }
};

function updateCartLength() {
    $('.js-cartBtn').find('.cart-badge').text(Object.keys(Storage.getData()).length);
}

updateCartLength();

$('.js-cartBtn').click(function () {
    $('#J_cartForm')
        .find('input').val(JSON.stringify(Storage.getData()))
        .end()
        .submit();
});