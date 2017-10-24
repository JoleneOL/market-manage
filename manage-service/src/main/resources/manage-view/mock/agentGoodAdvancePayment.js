/**
 * 预付货款的mock 数据
 * Created by CJ on 13/10/2017.
 */

Mock.setup({
    timeout: 500
});

Mock.Random.extend({
    toNull: function () {
        return null;
    }
});

Mock.mock(/\/manage\/agentGoodAdvancePayment/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|5": [
        {
            'id': '@id',
            'user': '@cname',
            'amount': '@integer(100, 10000)',
            'balance': '@integer(100, 10000)',
            'mobile': /^1([34578])\d{9}$/,
            'status': '@pick(["待处理","已拒绝","已成功"])',
            'approved': '@pick(@bool,@toNull)',
            'serial': null,
            'comment': null,
            'happenTime': '@datetime("yyyy-MM-dd")',
            'operator': '@cname',
            'approval': '@cname'

        }
    ]
});
