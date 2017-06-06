/**
 * Created by Neo on 2017/5/10.
 */
// 模拟延迟
// Mock.setup({
//     timeout: '1000'
// });

Mock.mock(/^\/api\/teamList\?rank=all&page=\d{1,100}$/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|20": [
        {
            name: "@cname",
            rank: '@pick(["总代理", "代理商", "经销商", "爱心天使"])',
            joinTime: '@now("yyyy-MM-dd")',
            phone: /^1([34578])\d{9}$/
        }
    ]
});

Mock.mock(/^\/api\/teamList\?rank=1&page=\d$/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|20": [
        {
            name: "@cname",
            rank: '总代理',
            joinTime: '@now("yyyy-MM-dd")',
            phone: /^1([34578])\d{9}$/
        }
    ]
});

Mock.mock(/^\/api\/teamList\?rank=2&page=\d$/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|20": [
        {
            name: "@cname",
            rank: '代理商',
            joinTime: '@now("yyyy-MM-dd")',
            phone: /^1([34578])\d{9}$/
        }
    ]
});
Mock.mock(/^\/api\/teamList\?rank=3&page=\d$/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|20": [
        {
            name: "@cname",
            rank: '经销商',
            joinTime: '@now("yyyy-MM-dd")',
            phone: /^1([34578])\d{9}$/
        }
    ]
});
Mock.mock(/^\/api\/teamList\?rank=4&page=\d$/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|0": [
        {
            name: "@cname",
            rank: '爱心天使',
            joinTime: '@now("yyyy-MM-dd")',
            phone: /^1([34578])\d{9}$/
        }
    ]
});

Mock.mock(/\/api\/orderList\?page=\d/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|20": [
        {
            orderId: '@id',
            orderTime: '@now("yyyy-MM-dd")',
            status: '成功',
            statusCode: 5,
            orderUser: "@cname",
            category: "饮水机",
            type: "u56 立式",
            total: '@integer(3000, 9999999)',
            package: '3年收费 730天',
            amount: '@integer(1, 100)',
            phone: /^1([34578])\d{9}$/
        }
    ]
});

Mock.mock(/\/api\/orderList\?status=\d&page=\d/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|20": [
        {
            orderId: '@id',
            orderTime: '@now("yyyy-MM-dd")',
            status: '待收货',
            statusCode: 1,
            orderUser: "@cname",
            category: "饮水机",
            type: "u56 立式",
            total: '@integer(3000, 9999999)',
            package: '3年收费 730天',
            amount: '@integer(1, 100)',
            phone: /^1([34578])\d{9}$/
        }
    ]
});
/**
 * equipmentStatus 暂定Number
 * 0: 正常使用中
 * 1: 维护中
 * 2: 维修中
 * 3: 退款中
 */

Mock.mock(/\/api\/equipmentList\?page=\d/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|10": [
        {
            equipmentId: '@id',
            id: '@id',
            equipmentStatus: '@integer(0, 2)',
            remainingTime: '@integer(1, 1095)',
            TDS: "@integer(1, 600)",
            installationAddress: '@county(true)',
            installationTime: '@now("yyyy-MM-dd")'
        }
    ]
});

// 看看不同的API
Mock.mock(/\/api\/commList\/all\?page=\d/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|10": [
        {
            id: '@id',
            commType: '@pick(["销售收益", "其他收益", "退款", "管理费"])',
            name: "@cname",
            commission: "@float(1, 100, 1, 2)",
            divided: '70%',
            commInfo: '滤芯01 3年收费 ￥6000',
            commTime: '@now("yyyy-MM-dd")'
        }
    ]
});

Mock.mock(/\/api\/commList\/today\?page=\d/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|10": [
        {
            id: '@id',
            commType: '@pick(["销售收益", "其他收益", "退款", "管理费"])',
            name: "@cname",
            commission: "@float(1, 100, 1, 2)",
            divided: '20%',
            commInfo: '滤芯01 3年收费 ￥3000',
            commTime: '@now("yyyy-MM-dd")'
        }
    ]
});

Mock.mock(/\/api\/commList\/month\?page=\d/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|10": [
        {
            id: '@id',
            commType: '@pick(["销售收益", "其他收益", "退款", "管理费"])',
            name: "@cname",
            commission: "@float(1, 100, 1, 2)",
            divided: '20%',
            commInfo: '滤芯01 3年收费 ￥3000',
            commTime: '@now("yyyy-MM-dd")'
        }
    ]
});

Mock.mock(/\/api\/commList\/previous\?page=\d/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|10": [
        {
            id: '@id',
            commType: '@pick(["销售收益", "其他收益", "退款", "管理费"])',
            name: "@cname",
            commission: "@float(1, 100, 1, 2)",
            divided: '20%',
            commInfo: '滤芯01 3年收费 ￥3000',
            commTime: '@now("yyyy-MM-dd")'
        }
    ]
});

Mock.mock(/\/api\/commList\/quarter\?page=\d/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|10": [
        {
            id: '@id',
            commType: '@pick(["销售收益", "其他收益", "退款", "管理费"])',
            name: "@cname",
            commission: "@float(1, 100, 1, 2)",
            divided: '20%',
            commInfo: '滤芯01 3年收费 ￥3000',
            commTime: '@now("yyyy-MM-dd")'
        }
    ]
});

Mock.mock(/\/api\/recommend/, "post", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data": {
        userName: '@pick(["老常", ""])'
    }

});

Mock.mock(/\/api\/authCode/, "post", {
    "resultCode": Mock.Random.boolean() ? 200 : 400,
    "resultMsg": "ok",
    "data": null
});

Mock.mock(/\/api\/mortgageCode/, "post", {
    "resultCode": "@pick([200, 400])",
    "resultMsg": "ok",
    "data": null
});