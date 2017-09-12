/**
 * Created by Neo on 2017/5/23.
 */

Mock.setup({
    timeout: 500
});

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

Mock.mock(/\/products\/list/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'code': '@string(10)',
            'brand': '@cname',
            'productName': '量子' + '@pick(["橱下净水器","空气净化器","食品优化宝"])',
            'category|0-2': '@pick(["净水机","净化器","优化宝","量子"])' + ' ',
            // 'supplier': '慈溪市海燕环保科技有限公司',
            'price': 400,
            'installFee': 180,
            'enable': '@boolean'
        }
    ]
});

Mock.mock(/\/goods\/list/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id|+1': 1,
            'productName': '@pick(["净水机","净化器","优化宝","量子"])',
            'channelName': null,
            'createTime': '@datetime("yyyy-MM-dd")',
            'enable': '@boolean'
        }
    ]
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


Mock.mock(/\/manage\/storage/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'storageType': '日日顺',
            'storage': '@region',
            'depotId': '@id',
            'product': '量子立式净水机',
            'productCode': '@name',
            'inventory': '@integer(1000, 9999)'
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

Mock.mock(/\/manage\/orderData\/logistics/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id': '@id',
            'mainOrderId': '@id',
            'supplierId': '@id',
            'orderId': '@id',
            'goods': '量子立式净水机',
            'amount': '@integer(1, 99)',
            'orderTime': '@datetime("yyyy-MM-dd")',
            'address': '@county(true)',
            'orderUser': '@cname',
            'mobile': /^1([34578])\d{9}$/,
            'storage': '@region',
            'deliverTime': '@datetime("yyyy-MM-dd")',
            'status': '@pick(["待发货", "待收货", "已收货", "待安装", "已安装"])',
            'stateCode': '@pick([0, 1, 2, 3, 4])'
        }
    ]
});

Mock.mock(/\/manage\/factoryOut/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
        {
            'id': '@id',
            'supplierId': '@id',
            // 'deliverQuantity': '@integer(1, 99)',
            'orderTime': '@datetime("yyyy-MM-dd")',
            'depotName': '@name',
            'address': '@county(true)',
            'contacts': '@cname',
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
            'orderTime': '@datetime("yyyy-MM-dd")',
            'goods': '量子立式净水机',
            'transferQuantity': '@integer(100, 999)',
            'transferStorage': '日日顺@region()仓',
            'deliverTime': '@datetime("yyyy-MM-dd")',
            'deliverStorage': '日日顺@region()仓',
            'contacts': '@cname',
            'mobile': /^1([34578])\d{9}$/,
            'status': '@pick(["待发货", "待收货", "已收货"])',
            'stateCode': '@pick([0, 1, 2])'
        }
    ]
});

Mock.mock(/\/storage\/transfer/, "get", {
    "draw": 1,
    "recordsTotal": 14,
    "recordsFiltered": 14,
    "data|4": [
        {
            'id': '@id',
            'storage': '@region',
            'quantity': '@integer(100, 999)',
            'distance': '@integer(100, 999)公里'
        }
    ]
});

Mock.mock(/\/product\/cat/, "get", {
    "draw": 1,
    "recordsTotal": 4,
    "recordsFiltered": 4,
    "data|4": [
        {
            'id': '@id',
            'category': '@pick(["饮水机", "净化器", "量子", "食品优化宝"])',
            'goods': ['量子立式净水机', '量子净化器', '量子芯片', '量子食品优化宝'],
        }
    ]
});

Mock.mock(/\/products\/category/, {
    "resultCode": 200,
    "resultMsg": "ok"
});

Mock.mock(/\/goods\/\d\/off/, {
    "resultCode": 200,
    "resultMsg": "ok"
});

Mock.mock(/\/goods\/\d\/on/, {

    "resultCode": 200,
    "resultMsg": "ok"
});
Mock.mock(/\/products\/\d/, 'delete', {
    "resultCode": 200,
    "resultMsg": "ok"
});

Mock.mock(/\/goods\/\d/, 'delete', {
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
            'orderBody': '1台汽车,2个轮子,3个座椅',
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
            'orderBody': '1台量子手机防辐射芯片,2个量子立式净水机（黑色）,3个量子立式净水机（白色）',
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
            'orderBody': '1台汽车,2个轮子,3个座椅,1台汽车,2个轮子,3个座椅,1台汽车,2个轮子,3个座椅',
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
            'orderBody': '1台量子手机防辐射芯片,2个量子立式净水机（黑色）,3个量子立式净水机（白色）,1个量子台式净水机（黑色）,2个量子台式净水机（白色）,3个量子厨下净水机 ,4个量子空气净化器,10个量子食品优化宝(金色）,100个量子食品优化宝（银色）',
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
            'orderBody': '1台汽车,2个轮子,3个座椅',
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
            'orderBody': '1台汽车,2个轮子,3个座椅',
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
            'orderBody': '1台汽车,2个轮子,3个座椅',
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
            'orderBody': '1台汽车,2个轮子,3个座椅',
            'quickDoneAble': '@boolean'
        }
    ]
});

Mock.mock(/\/orderData\/quickDone/, {
    "resultCode": 200,
    "resultMsg": "ok"
});
Mock.mock(/\/orderData\/logistics\/\d/, 'get', {
    'depots|5': [
        {
            'id': '@id',
            'name': '@region',
            'quantity': '@integer(100, 999)',
            'distance': '@integer(100, 999)'
        }
    ]
});

Mock.mock(/\/manage\/withdraws/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|5": [
        {
            'id': '@id',
            'user': '@cname',
            'userLevel': '@pick(经销商,市代理,爱心天使)',
            'payee': '@cname',
            'bank': '某银行',
            'account': '@id',
            'mobile': /^1([34578])\d{9}$/,
            'amount': '@integer(100, 10000)',
            'actualAmount': '@integer(100, 10000)',
            'requestTime': '@datetime("yyyy-MM-dd")',
            'logisticsCompany': '@pick(["-", "某公司"])',
            'logisticsCode': '@pick(["-", "@id"])',
            'comment': null,
            'transactionRecordNumber': null,
            'status': '@pick(["待审核","已拒绝","已成功"])',
            'statusCode': '@integer(2, 4)'
        }
    ]
});

// 剩余库存
Mock.mock(/\/api\/logisticsShip/, {
    "resultCode": 200,
    "resultMsg": "ok",
    "data": {
        1: {
            1: '2000',
            2: '1000',
            3: '200'
        },
        2: {
            1: '2000',
            2: '200',
            3: '400'
        },
        3: {
            1: '500',
            2: '200',
            3: '0'
        }
    }
});