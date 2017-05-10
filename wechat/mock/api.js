/**
 * Created by Neo on 2017/5/10.
 */
// 模拟延迟
Mock.setup({
    timeout: '2000'
});
/**
 * 使用正则超级(｡･∀･)ﾉﾞ嗨
 * resultCode 多个200，减少错误概率 ~囧~
 */
Mock.mock(/\/api\/teamList/, "get", {
    "resultCode": '@pick([200, 200, 200, 500])',
    "resultMsg": "ok",
    "data|10": [
        {
            name: "@cname",
            rank: '@pick(["会员", "代理商", "经销商", "普通用户"])',
            joinTime: '@datetime("yyyy-MM-dd")',
            phone: '@integer(10000000000, 18888888888)'
        }
    ]
});