/**
 * Created by CJ on 26/10/2017.
 */

Mock.setup({
    timeout: 500
});

Mock.mock(/\/manage\/login/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [{
        'id|+1': 1,
        'name': 'test' + '@string(3)',
        'mobile': /^1([34578])\d{9}$/,
        'createdTime': '@datetime("yyyy-MM-dd")',
        'level': '级别',
        'wechatID': '@title(1)' + '@natural(10, 1000)',
        'state': '@pick(启用, 禁用)',
        'stateCode': '@pick([0, 1])',
    }]
});