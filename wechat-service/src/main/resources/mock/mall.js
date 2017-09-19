Mock.setup({
    timeout: '1000'
});

Mock.mock(/\/api\/goodsList\?search=(.*)/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|0-5": [
        {
            goodsId: '@id',
            goodsName: '@csentence(50)',
            goodsImage: Mock.Random.image('357x357'),
            goodsDescribe: '@csentence',
            price: '@integer(3000, 10000)',
            salesVolume:'@integer(1000, 99999)'
        }
    ]
});