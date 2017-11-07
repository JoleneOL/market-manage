Mock.setup({
    timeout: 1000
});

Mock.mock(/\/manage\/commonProblemList/, {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|5": [
        {
            'id': '@id',
            'title': '@ctitle',
            /*'updateTime': '@datetime("yyyy-MM-dd")',*/
            'enableLabel': '@pick(启用, 禁用)',
            'enable': '@pick([true, false])',
            'isHotLabel':'@pick(展示,隐藏)',
            'weight':'50'
        }
    ]
});

Mock.mock(/\/help\/entry\/\d/, "delete", {
    "resultCode": 200,
    "resultMsg": "ok"
});