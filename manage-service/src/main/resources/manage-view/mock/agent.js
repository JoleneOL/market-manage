Mock.setup({
    timeout: 500
});

Mock.mock(/\/agent\/name\/\d/, {
    "resultCode": 200,
    "resultMsg": "ok"
});