/**
 * Created by Neo on 2017/5/10.
 */
// 模拟延迟
Mock.setup({
    timeout: '1000'
});

Mock.mock(/^\/api\/subordinate\?page=\d{1,100}$/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|20": [
        {
            id: '@id',
            name: "@cname",
            rank: '区县代理',
            joinTime: '@now("yyyy-MM-dd")',
            phone: /^1([34578])\d{9}$/,
            nextRank:3
        }
    ]
});

Mock.mock(/^\/api\/directly\?page=\d{1,100}$/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|20": [
        {
            name: "@cname",
            rank: '区县代理',
            joinTime: '@now("yyyy-MM-dd")',
            phone: /^1([34578])\d{9}$/
        },
        {
            name: "@cname",
            rank: '经销商',
            joinTime: '@now("yyyy-MM-dd")',
            phone: /^1([34578])\d{9}$/
        },
        {
            name: "@cname",
            rank: '爱心天使',
            joinTime: '@now("yyyy-MM-dd")',
            phone: /^1([34578])\d{9}$/
        }
    ]
});


Mock.mock(/^\/api\/teamList\?rank=all&page=\d{1,100}$/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|20": [
        {
            name: "@cname",
            rank: '区县代理',
            joinTime: '@now("yyyy-MM-dd")',
            phone: /^1([34578])\d{9}$/
        }
    ]
});

Mock.mock(/^\/api\/teamList\?rank=all&page=\d{1,100}$/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|20": [
        {
            name: "@cname",
            rank: '区县代理',
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
            rank: '市代理',
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
    "data|20": [
        {
            name: "@cname",
            rank: '爱心天使',
            joinTime: '@now("yyyy-MM-dd")',
            phone: /^1([34578])\d{9}$/
        }
    ]
});

Mock.mock(/^\/api\/teamList\?rank=5&page=\d$/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|20": [
        {
            name: "@cname",
            rank: '普通用户',
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
            orderTime: '@datetime("yyyy-MM-dd")',
            status: '成功',
            statusCode: 5,
            orderUser: "@cname",
            "goods":[
                {name:'量子立式净水机（黑色）', amount: '@integer(1, 100)'},
                {name:'食品优化宝（金色）', amount: '@integer(1, 100)'},
                {name:'立式净水机（白色）', amount: '@integer(1, 100)'}
            ],
            total: '@integer(3000, 9999999)',
            "hasInvoice|1-2": Mock.Random.boolean(),
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
            orderTime: '@datetime("yyyy-MM-dd")',
            status: '待收货',
            statusCode: 1,
            orderUser: "@cname",
            goods:[
                {name:'量子立式净水机（黑色）', amount: '@integer(1, 100)'},
                {name:'量子立式净水机（金色）', amount: '@integer(1, 100)'}
            ],
            total: '@integer(3000, 9999999)',
            "hasInvoice|1-2": Mock.Random.boolean(),
            phone: /^1([34578])\d{9}$/
        }
    ]
});

Mock.mock(/\/api\/orderList\?search=(.*)&page=\d/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data": [
        {
            orderId: '@id',
            orderTime: '@datetime("yyyy-MM-dd")',
            status: '成功',
            statusCode: 5,
            orderUser: "@cname",
            goods:[
                {name:'量子立式净水机（黑色）', amount: '@integer(1, 100)'}
            ],
            total: '@integer(3000, 9999999)',
            "hasInvoice|1-2": Mock.Random.boolean(),
            phone: /^1([34578])\d{9}$/
        }
    ]
});

Mock.mock(/\/api\/orderList\?status=\d&search=(.*)&page=\d/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data": [
        {
            orderId: '@id',
            orderTime: '@datetime("yyyy-MM-dd")',
            status: '待收货',
            statusCode: 1,
            orderUser: "@cname",
            category: "饮水机",
            type: "u56 立式",
            total: '@integer(3000, 9999999)',
            package: '3年收费 730天',
            amount: '@integer(1, 100)',
            "hasInvoice|1-2": Mock.Random.boolean(),
            phone: /^1([34578])\d{9}$/
        }
    ]
});

/**
 * equipmentStatus 暂定Number
 * 0: 正常使用中
 * 1: 维护中
 * 2: 已移机
 */

Mock.mock(/\/api\/equipmentList\?page=\d/, "get", {
    "resultCode": 200,
    "resultMsg": "ok",
    "data|2": [
        {
            equipmentId: '@id',
            id: '@id',
            model: '尼维柯立式净水机 SCRO-200LK-L',
            status: '@integer(0, 2)',
            years: '3年',
            cost: '3000',
            service: '更换滤芯',
            transferAddress: '@county(true)',
            transferPhone: /^1([34578])\d{9}$/,
            transferUser: '@cname',
            transferTime: '@now("yyyy-MM-dd")',
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

Mock.mock(/\/api\/commList\/pending\?page=\d/, "get", {
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

var uploaderImg = Mock.Random.image('228x178', '#50B347', '#FFF', 'Mock.js');

Mock.mock(/\/resourceUpload\/webUploader/, {
    "id": "filePath",
    "url": uploaderImg
});