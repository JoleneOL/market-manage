Mock.setup({
    timeout: '1000'
});

Mock.mock(/\/api\/goodsList\?productName=(.*)/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|0-5": [
        {
            id: '@id',
            productName: '@csentence(50)',
            goodsImage: Mock.Random.image('357x357'),
            description: '@csentence',
            price: '@integer(3000, 10000)',
            salesVolume:'@integer(1000, 99999)'
        }
    ]
});

Mock.mock(/\/api\/goodsList\?tag=(.*)&property=(.*)&price=(.*)/, {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|0-2": [
        {
            id: '@id',
            productName: '@csentence(50)',
            goodsImage: Mock.Random.image('357x357'),
            tags: '@csentence @csentence',
            price: '@integer(3000, 10000)',
            salesVolume:'@integer(1000, 99999)'
        }
    ]
});