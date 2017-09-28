/**
 * 伪造的销售人员
 * Created by CJ on 08/09/2017.
 */

Mock.mock(/\/manage\/salesmen/, "post", {});

Mock.mock(/\/manage\/salesmen\/\d\/rate/, "put", {});
Mock.mock(/\/manage\/salesmen\/\d\/rank/, "put", {});
Mock.mock(/\/manage\/salesmen\/\d\/disable/, "put", {});
Mock.mock(/\/manage\/salesmen\/\d\/enable/, "put", {});

Mock.mock(/\/manage\/salesmen/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [{
        'id|+1': 1,
        'name': 'test' + '@string(3)',
        'mobile': /^1([34578])\d{9}$/,
        'enableLabel': '@pick(启用, 禁用)',
        'enable': '@pick([true, false])',
        'rateLabel': '50%',
        'rate': 50,
        'rank': ''
    }]
});

// Mock.setup({
//     timeout: '99999'
// });
//
// Mock.mock(/\/stopIt/,{});