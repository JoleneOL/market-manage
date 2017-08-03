Mock.setup({
    timeout: '200-600'
});

Mock.mock(/products/, "get", {
    "draw": 1,
    "recordsTotal": 23,
    "recordsFiltered": 23,
    "data|10": [
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
