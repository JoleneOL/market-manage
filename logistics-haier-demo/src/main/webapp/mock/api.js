Mock.setup({
    timeout: '200-600'
});

// 仓库
Mock.mock(/store/, 'get', {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|3": [
        {
            'id|+1': 1,
            'province': '@province()',
            'city': '@city',
            'country': '@country',
            'detailAddress': '@cword(3,5)',
            'personalName': '@cname',
            'personalMobile': '15988888888',
            'code': '@word'
        }
    ]
});

Mock.mock(/products/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|3": [
        {
            'id|+1': 1,
            'code': '@word',
            'hrCode': '@word',
            'name': '@cword(3,5)',
            'brand': '@cword(3,5)',
            'category': '@cword(3,5)',
            'description': '@cword(5,10)',
            'SKU': '@word',
            'unit': '@cword(1)',
            'volumeLength': '@natural',
            'volumeWidth': '@natural',
            'volumeHeight': '@natural',
            'volumeWeight': '@natural'
        }
    ]
});
