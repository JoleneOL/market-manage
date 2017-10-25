Mock.setup({
    timeout: 500
});

Mock.mock(/\/login\/name\/\d/, {
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


Mock.mock(/\/login\/mobile\/\d/, {
    "resultCode": 200,
    "resultMsg": "ok"
});

Mock.mock(/\/subordinate\/list/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id': '@id',
            'name': '@cname',
            // 'address': '@county(true)',
            'mobile': /^1([34578])\d{9}$/,
            'createdTime': '@datetime("yyyy-MM-dd")',
            'earliestOrderTime': '@datetime("yyyy-MM-dd")',
            'orderTotal': '@integer(3600, 10000)'
        }
    ]
});

Mock.mock(/\/agentGoodAdvancePaymentJournal/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id': '@id',
            'orderId': '@id',
            'event': '@pick("increase","decrease")',
            'happenTime': '@datetime("yyyy-MM-dd")',
            'changedAbsMoney': '@integer(3600, 10000)',
            // 'balance': '@integer(3600, 10000)',
            'type': '@pick("购买","充值")'
        }
    ]
});