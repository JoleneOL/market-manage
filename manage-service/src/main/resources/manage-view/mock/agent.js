Mock.setup({
    timeout: 500
});

Mock.mock(/\/agent\/name\/\d/, {
    "resultCode": 200,
    "resultMsg": "ok"
});

Mock.mock(/\/agent\/superior\/\d/, {
    "resultCode": 200,
    "resultMsg": "ok",
    "data": {
        name: '@cname'
    }
});

Mock.mock(/\/api\/authCode/, "post", {
    "resultCode": 200,
    "resultMsg": "该手机号已经被注册",
    "data": null
});


Mock.mock(/\/agent\/mobile\/\d/, {
    "resultCode": 200,
    "resultMsg": "ok"
});