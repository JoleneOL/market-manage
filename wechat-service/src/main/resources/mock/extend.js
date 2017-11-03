Mock.setup({
    timeout: '1000'
});

Mock.mock(/\/api\/search\?title=(.*)/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|0-5": [
        {
            'helpId': '@id()',
            'helpTitle': '@ctitle()'
        }
    ]
});