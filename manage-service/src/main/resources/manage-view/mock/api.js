/**
 * Created by Neo on 2017/5/23.
 */
Mock.mock(/\/agentData\/list/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [{
        'id|+1': 1,
        'rank': '@province()总代理',
        'name': '@cname',
        'phone': '15988888888',
        'subordinate': "代理商（2%）14 经销商（2%）12 经纪人（1%）5200",
        'children|1-2': [{
            'id|+10': 1,
            'rank': '@county()代理商',
            'name': '@cname',
            'phone': '15988887777',
            'subordinate': "经销商（2%） 12 经纪人（1%）2000",
            'children|1-2': [{
                "id|+100": 1,
                "rank": "@cname()经销商",
                "name": "@cname",
                "phone": "15988880000",
                "subordinate": "经纪人（1%）23200"
            }]
        }]
    }]
});

Mock.mock(/\/refund\/list/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [{
        'id|+1': 1,
        'user': '@cname',
        'phone': '15988888888',
        'type': '滤芯 u56  立式',
        'code': '@id',
        'amount': 1,
        'time': '2017-09-09',
        'operator': '大大头',
        'status': '待处理',
        'statusCode': '@pick([0,1,2,3,4])'
    }]
});

Mock.mock(/\/afterSale\/list/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [{
        'id|+1': 1,
        'user': '@cname',
        'phone': '15988888888',
        'type': '滤芯 u56  立式',
        'code': '@id',
        'amount': 1,
        'time': '2017-09-09',
        'operator': '大大头',
        'status': '待处理',
        'statusCode': '@pick([0,1,2])'
    }]
});

Mock.mock(/\/product\/list/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [{
        'id|+1': 1,
        'category': '量子' + '@pick(["橱下净水器","空气净化器","食品优化宝"])',
        'type': '@string(10,15)',
        'supplier': '慈溪市海燕环保科技有限公司',
        'price': 400,
        'cost': 3000,
        'installFee': 180,
        'stagesTime': '@pick([3,6,9,12])',
        'stagesType': '花呗'
    }]
});

Mock.mock(/\/manage\/managers/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [{
        'id|+1': 1,
        'name': 'test' + '@string(3)',
        'department': '@pick(财务,技术,运营,客服,推广)',
        'realName': '@cname()',
        'wechatID': '@title(1)' + '@natural(10, 1000)',
        'role|1-3': '@pick(超管,普通,运营,客服,推广)' + ' ',
        'state': '@pick(启用, 禁用)',
        'stateCode': '@pick([0, 1])',
        'remark': ''
    }]
});

Mock.mock(/\/manage\/promotionRequests/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [{
        'id|+1': 1,
        'name': '@cname',
        'currentLevel': '爱心天使',
        'applicationLevel': '@pick(经销商,市代理,省代理)',
        'address': '@county(true)',
        'mobile': /^1([34578])\d{9}$/,
        'cardFront': Mock.Random.image('3120x4160', '#FF6600', '前面'),
        'cardBack': Mock.Random.image('240x151', '#50B347', '后面'),
        'businessLicense|0-1': Mock.Random.image('240x151', '#894FC4', '营业'),
        'paymentStatus': '@pick(["未支付", "已支付"])',
        'applicationDate': '@now("yyyy-MM-dd")',
        'operator': '@pick(["-", "@cname"])',
        'status': '@pick(["待处理", "已处理"])',
        'stateCode': '@pick([0, 1])'
    }]
});

Mock.mock(/\/manage\/mortgage/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data": [
        {
            'id': '@id',
            'orderId': '@id',
            'mortgageCode': '@word(5)@integer(100)',
            'userName': '@cname',
            'mobile': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'status': '待订单完成',
            'statusCode': 1
        },
        {
            'id': '@id',
            'orderId': '@id',
            'mortgageCode': '@word(5)@integer(100)',
            'userName': '@cname',
            'mobile': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'status': '待信审',
            'statusCode': 2
        },
        {
            'id': '@id',
            'orderId': '@id',
            'mortgageCode': '@word(5)@integer(100)',
            'userName': '@cname',
            'mobile': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'status': '信审中',
            'statusCode': 3
        },
        {
            'id': '@id',
            'orderId': '@id',
            'mortgageCode': '@word(5)@integer(100)',
            'userName': '@cname',
            'mobile': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'status': '信审被拒',
            'statusCode': 4
        },
        {
            'id': '@id',
            'orderId': '@id',
            'mortgageCode': '@word(5)@integer(100)',
            'userName': '@cname',
            'mobile': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'status': '待结算',
            'statusCode': 5
        },
        {
            'id': '@id',
            'orderId': '@id',
            'mortgageCode': '@word(5)@integer(100)',
            'userName': '@cname',
            'mobile': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'status': '已结算',
            'statusCode': 6
        }
    ]
});


Mock.mock(/\/products\/\d/, {
    "resultCode": 200,
    "resultMsg": "ok"
});

Mock.mock(/\/login\/\d\/disable/, "put", {
    "resultCode": 200,
    "resultMsg": "ok"
});

Mock.mock(/\/login\/\d\/password/, "put", "@string(10)");

Mock.mock(/\/login\/\d\/enable/, "put", {
    "resultCode": 200,
    "resultMsg": "ok"
});

Mock.mock(/\/manage\/promotionRequests\/\d\/approved/, "put", {
    "resultCode": 200,
    "resultMsg": "ok"
});

Mock.mock(/\/login\/\d/, "delete", {
    "resultCode": 200,
    "resultMsg": "ok"
});

var uploaderImg = Mock.Random.image('600x278', '#50B347', '#FFF', 'Mock.js');

Mock.mock(/\/resourceUpload\/webUploader/, {
    "id": "filePath",
    "url": uploaderImg
});

Mock.mock(/\/orderData\/manageableList/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data": [
        {
            'id': '@id',
            'orderId': '@id',
            'user': '@cname',
            'userLevel': '@pick(经销商,市代理,爱心天使)',
            'goods': '帅风立式净水器',
            'amount': '@integer(1, 100)',
            'orderUser': '@cname',
            'address': '@county(true)',
            'phone': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'method': '全额',
            'methodCode': 0,
            'total': '@integer(3600, 10000)',
            'operator': '@pick(["-", "@cname"])',
            'status': '待付款',
            'statusCode': 1,
            'quickDoneAble': false
        },
        {
            'id': '@id',
            'orderId': '@id',
            'user': '@cname',
            'userLevel': '@pick(经销商,市代理,爱心天使)',
            'goods': '帅风立式净水器',
            'amount': '@integer(1, 100)',
            'orderUser': '@cname',
            'address': '@county(true)',
            'phone': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'method': '全额',
            'methodCode': 0,
            'total': '@integer(3600, 10000)',
            'operator': '@pick(["-", "@cname"])',
            'status': '已付款',
            'statusCode': 8,
            'quickDoneAble': true
        },
        {
            'id': '@id',
            'orderId': '@id',
            'user': '@cname',
            'userLevel': '@pick(经销商,市代理,爱心天使)',
            'goods': '帅风立式净水器',
            'amount': '@integer(1, 100)',
            'orderUser': '@cname',
            'address': '@county(true)',
            'phone': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'method': '全额',
            'methodCode': 0,
            'total': '@integer(3600, 10000)',
            'operator': '@pick(["-", "@cname"])',
            'status': '待发货',
            'statusCode': 2,
            'quickDoneAble': true
        },
        {
            'id': '@id',
            'orderId': '@id',
            'user': '@cname',
            'userLevel': '@pick(经销商,市代理,爱心天使)',
            'goods': '帅风立式净水器',
            'amount': '@integer(1, 100)',
            'orderUser': '@cname',
            'address': '@county(true)',
            'phone': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'method': '全额',
            'methodCode': 0,
            'total': '@integer(3600, 10000)',
            'operator': '@pick(["-", "@cname"])',
            'status': '已发货',
            'statusCode': 9
        },
        {
            'id': '@id',
            'orderId': '@id',
            'user': '@cname',
            'userLevel': '@pick(经销商,市代理,爱心天使)',
            'goods': '帅风立式净水器',
            'amount': '@integer(1, 100)',
            'orderUser': '@cname',
            'address': '@county(true)',
            'phone': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'method': '全额',
            'methodCode': 0,
            'total': '@integer(3600, 10000)',
            'operator': '@pick(["-", "@cname"])',
            'status': '订单异常',
            'statusCode': 7
        },
        {
            'id': '@id',
            'orderId': '@id',
            'user': '@cname',
            'userLevel': '@pick(经销商,市代理,爱心天使)',
            'goods': '帅风立式净水器',
            'amount': '@integer(1, 100)',
            'orderUser': '@cname',
            'address': '@county(true)',
            'phone': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'method': '投融家',
            'methodCode': 2,
            'total': '@integer(3600, 10000)',
            'operator': '@pick(["-", "@cname"])',
            'status': '待信审',
            'statusCode': 2,
            'quickDoneAble': true
        },
        {
            'id': '@id',
            'orderId': '@id',
            'user': '@cname',
            'userLevel': '@pick(经销商,市代理,爱心天使)',
            'goods': '帅风立式净水器',
            'amount': '@integer(1, 100)',
            'orderUser': '@cname',
            'address': '@county(true)',
            'phone': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'method': '投融家',
            'methodCode': 2,
            'total': '@integer(3600, 10000)',
            'operator': '@pick(["-", "@cname"])',
            'status': '信审被拒',
            'statusCode': 4,
            'quickDoneAble': true
        },
        {
            'user': '@cname',
            'userLevel': '@pick(经销商,市代理,爱心天使)',
            'id': '@id',
            'orderId': '@id',
            'goods': '帅风立式净水器',
            'amount': '@integer(1, 100)',
            'orderUser': '@cname',
            'address': '@county(true)',
            'phone': /^1([34578])\d{9}$/,
            'orderTime': '@datetime("yyyy-MM-dd")',
            'method': '花呗分期',
            'methodCode': 3,
            'total': '@integer(3600, 10000)',
            'operator': '@pick(["-", "@cname"])',
            'status': '待发货',
            'statusCode': 2,
            'quickDoneAble': '@boolean'
        }
    ]
});

Mock.mock(/\/orderData\/quickDone/, {
    "resultCode": 200,
    "resultMsg": "ok"
});