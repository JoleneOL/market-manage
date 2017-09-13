/**
 * 发货相关互动
 * Created by CJ on 13/09/2017.
 */
// 如果要输入什么信息的话
// Mock.mock(/\/api\/logisticsShip/, {
//     "resultCode": 302,
//     "resultMsg": "把快递单号给我呀"
// });
// 成功->剩余库存
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