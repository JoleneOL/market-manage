/**
 * Created by Neo on 2017/5/23.
 */
Mock.mock(/\/agentData\/list/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
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
        }
    ]
});

Mock.mock(/\/refund\/list/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
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
        }
    ]
});

Mock.mock(/\/afterSale\/list/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
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
        }
    ]
});

Mock.mock(/\/product\/list/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id|+1': 1,
            'category': '量子' + '@pick(["橱下净水器","空气净化器","食品优化宝"])',
            'type': '@string(10,15)',
            'supplier': '慈溪市海燕环保科技有限公司',
            'price': 400,
            'cost': 3000,
            'installFee': 180,
            'stagesTime': '@pick([3,6,9,12])',
            'stagesType': '花呗'
        }
    ]
});

Mock.mock(/\/manage\/managers/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id|+1': 1,
            'name': 'test' + '@string(3)',
            'department': '@pick(财务,技术,运营,客服,推广)',
            'realName': '@cname()',
            'wechatID': '@title(1)' + '@natural(10, 1000)',
            'role|1-3': '@pick(超管,普通,运营,客服,推广)' + ' ',
            'state': '@pick(启用, 禁用)',
            'stateCode': '@pick([0, 1])',
            'remark': ''
        }
    ]
});

Mock.mock(/\/manage\/promotionRequests/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
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
        }
    ]
});

Mock.mock(/\/manage\/storage/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id': '@id',
            'orderId': '@id',
            'logistics': '日日顺',
            'storage': '@region',
            'goods': '量子立式净水机',
            'inventory': '@integer(1000, 9999)',
            'storageTime': '@datetime("yyyy-MM-dd")',
            'operator': '@cname'
        }
    ]
});

Mock.mock(/\/storage\/detail/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id': '@id',
            'orderId': '@id',
            'goods': '量子立式净水机',
            'goodsId': '@id',
            'storageTime': '@datetime("yyyy-MM-dd")',
            'operator': '@cname'
        }
    ]
});

Mock.mock(/\/message\/wait/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id': '@id',
            'message': '日日顺@region()仓帅风立式净水器已无货发货',
            'time': '@datetime("yyyy-MM-dd")',
            'status': '@pick(["待处理", "已处理"])',
            'stateCode': '@pick([0, 1])'
        }
    ]
});

Mock.mock(/\/message\/warn/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id': '@id',
            'message': '日日顺@region()仓帅风立式净水器已无货发货',
            'time': '@datetime("yyyy-MM-dd")',
            'status': '@pick(["待处理", "已处理"])',
            'stateCode': '@pick([0, 1])'
        }
    ]
});

Mock.mock(/\/url\/logistics/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id': '@id',
            'orderId': '@id',
            'goods': '量子立式净水机',
            'deliverQuantity':'@integer(1, 99)',
            'orderTime':'@datetime("yyyy-MM-dd")',
            'address':'@county(true)',
            'orderUser':'@cname',
            'mobile': /^1([34578])\d{9}$/,
            'logistics': '日日顺',
            'storage': '@region',
            'installation': '海尔',
            'deliverTime': '@datetime("yyyy-MM-dd")',
            'status': '@pick(["待发货", "待收货", "已收货", "待安装", "已安装"])',
            'stateCode': '@pick([0, 1, 2, 3, 4])'
        }
    ]
});

Mock.mock(/\/url\/factory/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id': '@id',
            'orderId': '@id',
            'goods': '量子立式净水机',
            'deliverQuantity':'@integer(1, 99)',
            'orderTime':'@datetime("yyyy-MM-dd")',
            'deliverFactory': '帅风',
            'deliverTime': '@datetime("yyyy-MM-dd")',
            'address':'@county(true)',
            'contacts':'@cname',
            'mobile': /^1([34578])\d{9}$/,
            'status': '@pick(["待发货", "待收货", "已收货"])',
            'stateCode': '@pick([0, 1, 2])'
        }
    ]
});

Mock.mock(/\/url\/storage/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id': '@id',
            'orderId': '@id',
            'orderTime':'@datetime("yyyy-MM-dd")',
            'goods': '量子立式净水机',
            'transferQuantity':'@integer(100, 999)',
            'transferStorage': '日日顺@region()仓',
            'deliverTime': '@datetime("yyyy-MM-dd")',
            'deliverStorage': '日日顺@region()仓',
            'contacts':'@cname',
            'mobile': /^1([34578])\d{9}$/,
            'status': '@pick(["待发货", "待收货", "已收货"])',
            'stateCode': '@pick([0, 1, 2])'
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

var uploaderImg = Mock.Random.image('228x178', '#50B347', '#FFF', 'Mock.js');

Mock.mock(/\/resourceUpload\/webUploader/, {
    "id": "filePath",
    "url": uploaderImg
});