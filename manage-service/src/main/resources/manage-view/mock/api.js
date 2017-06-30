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
            'statusCode': '@pick([0,1,2,3,4])'
        }
    ]
});