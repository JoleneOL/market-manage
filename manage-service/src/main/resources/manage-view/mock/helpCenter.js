Mock.setup({
    timeout: 1000
});

Mock.mock(/\/api\/helpCenter/, {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|5": [
        {
            'id': '@id',
            'title': '@ctitle',
            'type': '@pick(账号设置,转账服务,佣金提现,订单问题,其他功能)'
        }
    ]
});

Mock.mock(/\/help\/entry\/\d/, "delete", {
    "resultCode": 200,
    "resultMsg": "ok"
});